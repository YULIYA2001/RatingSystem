package by.ratingsystem.controller;

import by.ratingsystem.dto.CommentAndSellerCreateDto;
import by.ratingsystem.dto.CommentFullReadDto;
import by.ratingsystem.model.enums.Status;
import by.ratingsystem.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentFullReadDto>> getSellerComments(@RequestParam(required = false) Long sellerId,
                                                                      @RequestParam(required = false) Boolean verifiedSeller,
                                                                      @RequestParam(required = false) Status status) {
        return new ResponseEntity<>(commentService.getAll(sellerId, verifiedSeller, status), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CommentFullReadDto> createCommentWithNewSellerProfile(@RequestBody CommentAndSellerCreateDto dto) {
        return new ResponseEntity<>(commentService.createWithNewSellerProfile(dto), HttpStatus.OK);
    }


    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentFullReadDto>> approveComments(@RequestBody List<Long> ids) {
        return new ResponseEntity<>(commentService.changeStatus(ids, Status.APPROVED), HttpStatus.OK);
    }

    @PostMapping("/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentFullReadDto>> rejectComments(@RequestBody List<Long> ids) {
        return new ResponseEntity<>(commentService.changeStatus(ids, Status.REJECTED), HttpStatus.OK);
    }
}
