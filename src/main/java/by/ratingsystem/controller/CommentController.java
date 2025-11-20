package by.ratingsystem.controller;

import by.ratingsystem.dto.CommentFullReadDto;
import by.ratingsystem.model.Status;
import by.ratingsystem.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
//    @PreAuthorize(ADMIN)
    public ResponseEntity<List<CommentFullReadDto>> getSellerComments(@RequestParam(required = false) Long sellerId,
                                                                      @RequestParam(required = false) Status status) {
        return new ResponseEntity<>(commentService.getAll(sellerId, status), HttpStatus.OK);
    }

//    @PostMapping
//    //    @PreAuthorize(ADMIN)
//    public ResponseEntity<List<CommentFullReadDto>> getApproveComments(@RequestBody List<Long> commentIds) {
//        return null;
//    }
}
