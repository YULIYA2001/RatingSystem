package by.ratingsystem.repository;

import by.ratingsystem.model.Comment;
import by.ratingsystem.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndSellerId(Long id, Long sellerId);
    Optional<Comment> findByAuthorIdAndSellerId(Long authorId, Long sellerId);
    List<Comment> findBySellerIdAndStatusOrderByUpdatedAtDesc(Long authorId, Status status);
    List<Comment> findBySellerIdOrderByUpdatedAtDesc(Long sellerId);
    List<Comment> findByStatusOrderByUpdatedAtDesc(Status status);
    List<Comment> findAllByOrderByUpdatedAtDesc();
    List<Comment> findByIdInAndStatusAndVerifiedSeller(List<Long> ids, Status status, boolean verifiedSeller);
}
