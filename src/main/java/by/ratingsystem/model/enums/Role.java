package by.ratingsystem.model.enums;

public enum Role {
    ADMIN,
    SELLER,
    ANONYM;

    public static Long getAnonymId() {
        return 0L;
    }
}
