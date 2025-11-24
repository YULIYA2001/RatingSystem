package by.ratingsystem.dto;

import by.ratingsystem.dto.seller.SellerProfileCreateDto;

public class CommentAndSellerCreateDto {
    private CommentCreateDto commentDto;
    private SellerProfileCreateDto sellerDto;

    public CommentAndSellerCreateDto(CommentCreateDto commentDto, SellerProfileCreateDto sellerDto) {
        this.commentDto = commentDto;
        this.sellerDto = sellerDto;
    }

    public CommentCreateDto getCommentDto() {
        return commentDto;
    }

    public void setCommentDto(CommentCreateDto commentDto) {
        this.commentDto = commentDto;
    }

    public SellerProfileCreateDto getSellerDto() {
        return sellerDto;
    }

    public void setSellerDto(SellerProfileCreateDto sellerDto) {
        this.sellerDto = sellerDto;
    }
}
