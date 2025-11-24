package by.ratingsystem.controller;

import by.ratingsystem.dto.comment.CommentCreateDto;
import by.ratingsystem.dto.comment.CommentFullReadDto;
import by.ratingsystem.dto.comment.CommentReadDto;
import by.ratingsystem.model.enums.Role;
import by.ratingsystem.model.enums.Status;
import by.ratingsystem.security.jwt.JwtUserDetails;
import by.ratingsystem.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sellers/{sellerId}/comments")
public class SellersCommentController {
    private static final Long ANONYM = Role.getAnonymId();

    private final CommentService commentService;

    public SellersCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentFullReadDto> addComment(@PathVariable Long sellerId,
                                                         @RequestBody CommentCreateDto commentDto,
                                                         @AuthenticationPrincipal JwtUserDetails authenticatedUser) {
        Long authorId = authenticatedUser == null ? ANONYM : authenticatedUser.getId();
        return new ResponseEntity<>(commentService.create(sellerId, authorId, commentDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<? extends CommentReadDto>> getSellerComments(
            @PathVariable Long sellerId,
            @RequestParam(required = false) Status status,
            @AuthenticationPrincipal JwtUserDetails authenticatedUser) {
        boolean isAdmin = authenticatedUser != null && authenticatedUser.isAdmin();

        if (!isAdmin) {
            status = Status.APPROVED;
        }

        return new ResponseEntity<>(commentService.getAll(sellerId, status, isAdmin), HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<? extends CommentReadDto> getComment(
            @PathVariable Long sellerId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal JwtUserDetails authenticatedUser
    ) {
        boolean isAdmin = authenticatedUser != null && authenticatedUser.isAdmin();

        if (isAdmin) {
            return new ResponseEntity<>(commentService.getById(sellerId, commentId, null), HttpStatus.OK);
        }

        Long currentUserId = authenticatedUser == null ? ANONYM : authenticatedUser.getId();
        return new ResponseEntity<>(commentService.getById(sellerId, commentId, currentUserId), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HttpStatus> deleteComment(
            @PathVariable Long sellerId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal JwtUserDetails authenticatedUser
    ) throws AccessDeniedException {
        boolean isAdmin = authenticatedUser != null && authenticatedUser.isAdmin();

        if (isAdmin) {
            commentService.delete(sellerId, commentId, null);
        } else {
            Long currentUserId = authenticatedUser == null ? ANONYM : authenticatedUser.getId();
            commentService.delete(sellerId, commentId, currentUserId);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentFullReadDto> updateComment(
            @PathVariable Long sellerId,
            @RequestBody CommentCreateDto commentDto,
            @AuthenticationPrincipal JwtUserDetails authenticatedUser
    ) throws AccessDeniedException {
        Long currentUserId = authenticatedUser == null ? ANONYM : authenticatedUser.getId();
        return new ResponseEntity<>(commentService.update(sellerId, commentDto, currentUserId), HttpStatus.OK);
    }
}
