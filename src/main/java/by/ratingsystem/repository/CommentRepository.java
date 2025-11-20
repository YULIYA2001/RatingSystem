package by.ratingsystem.repository;

import by.ratingsystem.model.Comment;
import by.ratingsystem.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySellerIdAndStatusOrderByUpdatedAtDesc(Long authorId, Status status);
    List<Comment> findBySellerIdOrderByUpdatedAtDesc(Long sellerId);
    Optional<Comment> findByIdAndSellerId(Long id, Long sellerId);
    List<Comment> findByStatusOrderByUpdatedAtDesc(Status status);
    List<Comment> findAllByOrderByUpdatedAtDesc();
    Optional<Comment> findByAuthorIdAndSellerId(Long id, Long id1);
    List<Comment> findByIdInAndStatus(List<Long> ids, Status status);
}
