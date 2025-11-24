package by.ratingsystem.service;

import by.ratingsystem.dto.CommentAndSellerCreateDto;
import by.ratingsystem.dto.CommentCreateDto;
import by.ratingsystem.dto.CommentFullReadDto;
import by.ratingsystem.dto.CommentReadDto;
import by.ratingsystem.dto.ShortSellerProfileReadDto;
import by.ratingsystem.model.Comment;
import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.model.enums.Status;
import by.ratingsystem.model.User;
import by.ratingsystem.repository.CommentRepository;
import by.ratingsystem.repository.SellerProfileRepository;
import by.ratingsystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentService {
    // TODO refactor duplicate logic: combine "get..." and "delete..." methods
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final RatingService ratingService;
    private final SellerService sellerService;


    private static final Long ANONYM = 0L;  // TODO extract

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, SellerProfileRepository sellerProfileRepository, RatingService ratingService, SellerService sellerService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.sellerProfileRepository = sellerProfileRepository;
        this.ratingService = ratingService;
        this.sellerService = sellerService;
    }

    @Transactional
    public CommentFullReadDto create(Long sellerId, CommentCreateDto commentDto) {
        User author = userRepository.findById(commentDto.getAuthorId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        SellerProfile seller = sellerProfileRepository.findById(sellerId).orElseThrow(() -> new EntityNotFoundException("Seller not found"));

        Comment comment;
        Optional<Comment> oldComment = commentRepository.findByAuthorIdAndSellerId(author.getId(), seller.getId());
        if (oldComment.isPresent()) {
            comment = oldComment.get();

            if (comment.getStatus() == Status.APPROVED) {
                ratingService.reduceAndSaveRating(seller.getRating(), comment.getRatingMark());
            }

            comment.setMessage(commentDto.getMessage());
            comment.setRatingMark(commentDto.getRatingMark());
            comment.setStatus(Status.PENDING);
        } else {
            comment = new Comment();
            comment.setMessage(commentDto.getMessage());
            comment.setRatingMark(commentDto.getRatingMark());
            comment.setStatus(Status.PENDING);
            comment.setSeller(seller);
            comment.setAuthor(author);
            comment.setVerifiedSeller(true);
        }

        return mapToFullReadDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentFullReadDto createWithNewSellerProfile(CommentAndSellerCreateDto dto) {
        SellerProfile seller = sellerService.create(dto.getSellerDto(), null);

        CommentCreateDto commentDto = dto.getCommentDto();

        User author = userRepository.findById(commentDto.getAuthorId()).orElseThrow(() -> new EntityNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setMessage(commentDto.getMessage());
        comment.setRatingMark(commentDto.getRatingMark());
        comment.setStatus(Status.PENDING);
        comment.setSeller(seller);
        comment.setAuthor(author);
        comment.setVerifiedSeller(false);

        return mapToFullReadDto(commentRepository.save(comment));
    }

    private  <T> List<T> getAll(Long sellerId, Status status, Function<Comment, T> mapper) {
        if (!sellerProfileRepository.existsById(sellerId)) {
            throw new EntityNotFoundException("Seller profile with id=%s not found".formatted(sellerId));
        }

        List<Comment> comments = status != null
                    ? commentRepository.findBySellerIdAndStatusOrderByUpdatedAtDesc(sellerId, status)
                    : commentRepository.findBySellerIdOrderByUpdatedAtDesc(sellerId);

        return comments.stream().map(mapper).toList();
    }

    @Transactional(readOnly = true)
    public List<? extends CommentReadDto> getAll(Long sellerId, Status status, boolean isAdmin) {
        if (isAdmin) {
            return getAll(sellerId, status, this::mapToFullReadDto);
        }
        return getAll(sellerId, status, this::mapToReadDto);
    }

    private Comment getById(Long sellerId, Long commentId) {
        if (!sellerProfileRepository.existsById(sellerId)) {
            throw new EntityNotFoundException("Seller profile with id=%d not found".formatted(sellerId));
        }

       return commentRepository.findByIdAndSellerId(commentId, sellerId).orElseThrow(() ->
                new EntityNotFoundException(
                        "Comment with id=%d not found among seller (id=%d) comments".formatted(commentId, sellerId)
                ));
    }

    @Transactional(readOnly = true)
    public CommentReadDto getForUserById(Long sellerId, Long commentId, Long currentUserId) {
        Comment comment = getById(sellerId, commentId);

        if (!canViewComment(comment, currentUserId)) {
            throw new EntityNotFoundException("Comment not found");
        }

        return mapToReadDto(comment);
    }

    private boolean isAuthor(Long currentUserId, Long commentAuthorId) {
        return !Objects.equals(currentUserId, ANONYM)
                && Objects.equals(currentUserId, commentAuthorId);
    }

    // allowed: all approved, all owned by a registered user
    private boolean canViewComment(Comment comment, Long currentUserId) {
        return comment.getStatus() == Status.APPROVED
                || isAuthor(currentUserId, comment.getAuthor().getId());
    }

    @Transactional(readOnly = true)
    public List<CommentFullReadDto> getAll(Long sellerId, Boolean verifiedSeller, Status status) {
        List<Comment> comments;

        if (sellerId != null) {
            if (!sellerProfileRepository.existsById(sellerId)) {
                throw new EntityNotFoundException("Seller profile with id=%s not found".formatted(sellerId));
            }

            comments = status != null
                    ? commentRepository.findBySellerIdAndStatusOrderByUpdatedAtDesc(sellerId, status)
                    : commentRepository.findBySellerIdOrderByUpdatedAtDesc(sellerId);
        } else {
            comments = status != null
                    ? commentRepository.findByStatusOrderByUpdatedAtDesc(status)
                    : commentRepository.findAllByOrderByUpdatedAtDesc();
        }

        if (verifiedSeller != null) {
            comments = comments.stream().filter(comment -> comment.isVerifiedSeller() == verifiedSeller).toList();
        }

        return comments.stream().map(this::mapToFullReadDto).toList();
    }

    @Transactional(readOnly = true)
    public CommentFullReadDto getForAdminById(Long sellerId, Long commentId) {
        return mapToFullReadDto(getById(sellerId, commentId));
    }

    @Transactional
    public void deleteForAdmin(Long sellerId, Long commentId) {
        Comment comment = getById(sellerId, commentId);

        if (comment.getStatus() == Status.APPROVED) {
            ratingService.reduceAndSaveRating(comment.getSeller().getRating(), comment.getRatingMark());
        }

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void deleteForUser(Long sellerId, Long commentId, Long currentUserId) throws AccessDeniedException {
        Comment comment = getById(sellerId, commentId);

        if (!isAuthor(currentUserId, comment.getAuthor().getId())) {
            throw new AccessDeniedException("Cannot delete not your comment");
        }

        if (comment.getStatus() == Status.APPROVED) {
            ratingService.reduceAndSaveRating(comment.getSeller().getRating(), comment.getRatingMark());
        }

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentFullReadDto update(Long sellerId, CommentCreateDto commentCreateDto, Long currentUserId) throws AccessDeniedException {
        Comment comment = getById(sellerId, commentCreateDto.getId());

        if (!isAuthor(currentUserId, comment.getAuthor().getId())) {
            throw new AccessDeniedException("Cannot change not your comment");
        }

        if (comment.getStatus() == Status.APPROVED) {
            ratingService.reduceAndSaveRating(comment.getSeller().getRating(), comment.getRatingMark());
        }

        comment.setMessage(commentCreateDto.getMessage());
        comment.setRatingMark(commentCreateDto.getRatingMark());
        comment.setStatus(Status.PENDING);

        return mapToFullReadDto(comment);
    }

    @Transactional
    public List<CommentFullReadDto> changeStatus(List<Long> ids, Status status) {
        List<Comment> comments = commentRepository.findByIdInAndStatusAndVerifiedSeller(ids, Status.PENDING, true);
        if (comments.isEmpty()) {
            throw new EntityNotFoundException("No PENDING comment with verified seller was found. First verify sellers");
        }

        comments.forEach(comment -> comment.setStatus(status));

        Map<SellerProfile, IntSummaryStatistics> result = comments.stream()
                .collect(Collectors.groupingBy(
                        Comment::getSeller,
                        Collectors.summarizingInt(Comment::getRatingMark)
                ));

        if (status == Status.APPROVED) {
            result.forEach((seller, stats) -> ratingService.increaseAndSaveRating(
                    seller.getRating(),
                    (int) stats.getSum(),
                    (int) stats.getCount()
            ));
        }

        return comments.stream().map(this::mapToFullReadDto).toList();
    }

    public CommentReadDto mapToReadDto(Comment comment) {
        return new CommentReadDto(
                comment.getId(),
                comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName().charAt(0) + ".",
                comment.getSeller().getNickname(),
                comment.getMessage(),
                comment.getRatingMark(),
                comment.getUpdatedAt()
        );
    }

    public CommentFullReadDto mapToFullReadDto(Comment comment) {
        return new CommentFullReadDto(
                mapToReadDto(comment),
                comment.getStatus().name(),
                comment.isVerifiedSeller(),
                new ShortSellerProfileReadDto(
                        comment.getSeller().getId(),
                        comment.getSeller().getNickname()
                )
        );
    }
}
