package by.ratingsystem.dto.comment;

import java.time.LocalDateTime;

public class CommentReadDto {
    private Long id;
    private String author;
    private String seller;
    private String message;
    private int ratingMark;
    private LocalDateTime lastUpdate;

    public CommentReadDto(Long id, String author, String seller, String message,
                          int ratingMark, LocalDateTime lastUpdate) {
        this.id = id;
        this.author = author;
        this.seller = seller;
        this.message = message;
        this.ratingMark = ratingMark;
        this.lastUpdate = lastUpdate;
    }

    public CommentReadDto(CommentReadDto crDto) {
        this(crDto.id, crDto.author, crDto.seller, crDto.message, crDto.ratingMark, crDto.lastUpdate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRatingMark() {
        return ratingMark;
    }

    public void setRatingMark(int ratingMark) {
        this.ratingMark = ratingMark;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}