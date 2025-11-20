package by.ratingsystem.dto;

public class CommentCreateDto {
    private Long id;
    private Long authorId;  // TODO may be take from authorization
    private String message;
    private int ratingMark;

    public CommentCreateDto(Long id, Long authorId, String message, int ratingMark) {
        this.id = id;
        this.authorId = authorId;
        this.message = message;
        this.ratingMark = ratingMark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
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
}
