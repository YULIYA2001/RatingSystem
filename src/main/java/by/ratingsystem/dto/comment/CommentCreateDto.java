package by.ratingsystem.dto.comment;

public class CommentCreateDto {
    private Long id;
    private String message;
    private int ratingMark;

    public CommentCreateDto(Long id, String message, int ratingMark) {
        this.id = id;
        this.message = message;
        this.ratingMark = ratingMark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
