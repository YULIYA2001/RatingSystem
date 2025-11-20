package by.ratingsystem.dto;

public class CommentFullReadDto extends CommentReadDto {
    private String status;
    private boolean isVerifiedSeller;
    private ShortSellerProfileReadDto sellerProfile;

    public CommentFullReadDto(CommentReadDto commentReadDto,
                              String status,
                              boolean isVerifiedSeller,
                              ShortSellerProfileReadDto sellerProfile) {
        super(commentReadDto);
        this.status = status;
        this.isVerifiedSeller = isVerifiedSeller;
        this.sellerProfile = sellerProfile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isVerifiedSeller() {
        return isVerifiedSeller;
    }

    public void setVerifiedSeller(boolean verifiedSeller) {
        isVerifiedSeller = verifiedSeller;
    }

    public ShortSellerProfileReadDto getSellerProfile() {
        return sellerProfile;
    }

    public void setSellerProfile(ShortSellerProfileReadDto sellerProfile) {
        this.sellerProfile = sellerProfile;
    }
}
