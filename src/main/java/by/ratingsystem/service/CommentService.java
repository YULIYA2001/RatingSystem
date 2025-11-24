package by.ratingsystem.service;

import by.ratingsystem.dto.comment.CommentAndSellerCreateDto;
import by.ratingsystem.dto.comment.CommentCreateDto;
import by.ratingsystem.dto.comment.CommentFullReadDto;
import by.ratingsystem.dto.comment.CommentReadDto;
import by.ratingsystem.dto.seller.ShortSellerProfileReadDto;
import by.ratingsystem.model.Comment;
import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.model.User;
import by.ratingsystem.model.enums.Role;
import by.ratingsystem.model.enums.Status;
import by.ratingsystem.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentService {
    // TODO refactor duplicate logic "getAll" methods
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final RatingService ratingService;
    private final SellerService sellerService;

    public CommentService(CommentRepository commentRepository,
                          UserService userService,
                          RatingService ratingService,
                          SellerService sellerService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.ratingService = ratingService;
        this.sellerService = sellerService;
    }

    @Transactional
    public CommentFullReadDto create(Long sellerId, Long authorId, CommentCreateDto commentDto) {
        User author = userService.getById(authorId);
        SellerProfile seller = sellerService.getById(sellerId);

        Comment comment = commentRepository
                .findByAuthorIdAndSellerId(author.getId(), seller.getId())
                .map(old -> buildCommentForReplacement(commentDto, seller, old))
                .orElseGet(() -> buildComment(commentDto, seller, author, true));

        return mapToFullReadDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentFullReadDto createWithNewSellerProfile(Long authorId, CommentAndSellerCreateDto dto) {
        SellerProfile seller = sellerService.create(dto.getSellerDto(), null);
        User author = userService.getById(authorId);
        CommentCreateDto commentDto = dto.getCommentDto();

        Comment comment = buildComment(commentDto, seller, author, false);

        return mapToFullReadDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public CommentReadDto getById(Long sellerId, Long commentId, Long currentUserId) {
        Comment comment = getById(sellerId, commentId);

        if (currentUserId != null && !canViewComment(comment, currentUserId)) {
            throw new EntityNotFoundException("Comment not found");
        }

        return currentUserId == null ? mapToFullReadDto(comment) : mapToReadDto(comment);
    }

    @Transactional(readOnly = true)
    public List<? extends CommentReadDto> getAll(Long sellerId, Status status, boolean isAdmin) {
        if (isAdmin) {
            return getAll(sellerId, status, this::mapToFullReadDto);
        }
        return getAll(sellerId, status, this::mapToReadDto);
    }

    @Transactional(readOnly = true)
    public List<CommentFullReadDto> getAll(Long sellerId, Boolean verifiedSeller, Status status) {
        List<Comment> comments;

        if (sellerId != null) {
            sellerService.getById(sellerId);

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

    @Transactional
    public void delete(Long sellerId, Long commentId, Long currentUserId) {
        Comment comment = getById(sellerId, commentId);

        if (currentUserId != null && !isAuthor(currentUserId, comment.getAuthor().getId())) {
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
        List<Comment> comments = commentRepository
                .findByIdInAndStatusAndVerifiedSeller(ids, Status.PENDING, true);
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

    private Comment buildComment(CommentCreateDto commentDto,
                                 SellerProfile seller,
                                 User author,
                                 boolean isVerifiedSeller) {
        Comment comment = new Comment();
        comment.setMessage(commentDto.getMessage());
        comment.setRatingMark(commentDto.getRatingMark());
        comment.setStatus(Status.PENDING);
        comment.setSeller(seller);
        comment.setAuthor(author);
        comment.setVerifiedSeller(isVerifiedSeller);

        return comment;
    }

    private Comment buildCommentForReplacement(CommentCreateDto commentDto,
                                               SellerProfile seller,
                                               Comment oldComment) {
        if (oldComment.getStatus() == Status.APPROVED) {
            ratingService.reduceAndSaveRating(seller.getRating(), oldComment.getRatingMark());
        }

        oldComment.setMessage(commentDto.getMessage());
        oldComment.setRatingMark(commentDto.getRatingMark());
        oldComment.setStatus(Status.PENDING);

        return oldComment;
    }

    private  <T> List<T> getAll(Long sellerId, Status status, Function<Comment, T> mapper) {
        sellerService.getById(sellerId);

        List<Comment> comments = status != null
                ? commentRepository.findBySellerIdAndStatusOrderByUpdatedAtDesc(sellerId, status)
                : commentRepository.findBySellerIdOrderByUpdatedAtDesc(sellerId);

        return comments.stream().map(mapper).toList();
    }

    private Comment getById(Long sellerId, Long commentId) {
        sellerService.getById(sellerId);

        return commentRepository.findByIdAndSellerId(commentId, sellerId).orElseThrow(() ->
                new EntityNotFoundException(
                        "Comment with id=%d not found among seller (id=%d) comments".formatted(commentId, sellerId)
                ));
    }

    private boolean isAuthor(Long currentUserId, Long commentAuthorId) {
        return !Objects.equals(currentUserId, Role.ANONYM_ID)
                && Objects.equals(currentUserId, commentAuthorId);
    }

    // allowed for: all approved, all owned by a registered user
    private boolean canViewComment(Comment comment, Long currentUserId) {
        return comment.getStatus() == Status.APPROVED
                || isAuthor(currentUserId, comment.getAuthor().getId());
    }

    private CommentReadDto mapToReadDto(Comment comment) {
        return new CommentReadDto(
                comment.getId(),
                comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName().charAt(0) + ".",
                comment.getSeller().getNickname(),
                comment.getMessage(),
                comment.getRatingMark(),
                comment.getUpdatedAt()
        );
    }

    private CommentFullReadDto mapToFullReadDto(Comment comment) {
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
