package by.ratingsystem.controller;

import by.ratingsystem.dto.CommentCreateDto;
import by.ratingsystem.dto.CommentFullReadDto;
import by.ratingsystem.dto.CommentReadDto;
import by.ratingsystem.model.Status;
import by.ratingsystem.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/sellers/{sellerId}/comments")
public class SellersCommentController {
    private final CommentService commentService;

    public SellersCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

//* PUT         /users/:id/comments: Обновить комментарий.

    @PostMapping
    public ResponseEntity<CommentFullReadDto> addComment(@PathVariable Long sellerId,
                                                         @RequestBody CommentCreateDto commentDto) {
        return new ResponseEntity<>(commentService.create(sellerId, commentDto), HttpStatus.CREATED);
    }

    @GetMapping
//    @PreAuthorize(ALL + ADMIN)
    public ResponseEntity<List<? extends CommentReadDto>> getSellerComments(
            @PathVariable Long sellerId,
            @RequestParam(required = false) Status status) {
//            , Authentication authentication) {
        boolean isAdmin = true;    // authentication...;

        if (!isAdmin) {
            status = Status.APPROVED;
        }

        return new ResponseEntity<>(commentService.getAll(sellerId, status, isAdmin), HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    //    @PreAuthorize(ALL + ADMIN)
    public ResponseEntity<? extends CommentReadDto> getComment(@PathVariable Long sellerId,
                                                               @PathVariable Long commentId) {
//                                                               , Authentication authentication) {
        boolean isAdmin = true;    // authentication...;

        if (isAdmin) {
            return new ResponseEntity<>(commentService.getForAdminById(sellerId, commentId), HttpStatus.OK);
        }

        Long currentUserId = 3L;
        return new ResponseEntity<>(commentService.getForUserById(sellerId, commentId, currentUserId), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    //    @PreAuthorize(USER + ADMIN)
    public ResponseEntity<Void> deleteComment(@PathVariable Long sellerId, @PathVariable Long commentId) throws AccessDeniedException {
        boolean isAdmin = true;

        if (isAdmin) {
            commentService.deleteForAdmin(sellerId, commentId);
        } else {
            Long currentUserId = 3L;
            commentService.deleteForUser(sellerId, commentId, currentUserId);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    //    @PreAuthorize(USER)
    public ResponseEntity<CommentFullReadDto> updateComment(@PathVariable Long sellerId,
                                                            @RequestBody CommentCreateDto commentDto) throws AccessDeniedException {
        Long currentUserId = 3L;
        return new ResponseEntity<>(commentService.update(sellerId, commentDto, currentUserId), HttpStatus.OK);
    }
}
