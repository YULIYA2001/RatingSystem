package by.ratingsystem.controller;

import by.ratingsystem.dto.SellerProfileCreateDto;
import by.ratingsystem.dto.SellerProfileFullReadDto;
import by.ratingsystem.dto.SellerProfileReadDto;
import by.ratingsystem.model.Status;
import by.ratingsystem.service.SellerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping
    // @PreAuthorize(USER/ADMIN)
    public ResponseEntity<SellerProfileFullReadDto> createSellerProfile(@RequestBody SellerProfileCreateDto sellerCreateDto) {
        // get authorized userId
        Long userId = 3L;
        return new ResponseEntity<>(sellerService.createSellerProfile(sellerCreateDto, userId), HttpStatus.CREATED);
    }

    @GetMapping
    // @PreAuthorize(ADMIN ALL)
    public ResponseEntity<List<? extends SellerProfileReadDto>> getSellerProfiles(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) BigDecimal maxRating,
            @RequestParam(required = false) Long gameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        boolean isAdmin = true;
        if (!isAdmin) {
            status = Status.APPROVED;
        }

        return new ResponseEntity<>(
                sellerService.findAll(status, minRating, maxRating, gameId, PageRequest.of(page, size), isAdmin),
                HttpStatus.OK
        );
    }

    @GetMapping("/top-best")
    public ResponseEntity<List<SellerProfileFullReadDto>> getTopSellerProfiles(@RequestParam(required = false) Integer topCount) {
        return new ResponseEntity<>(sellerService.findTopRatingSellers(topCount), HttpStatus.OK);
    }

    @PostMapping("/{id}/approve")
    // @PreAuthorize(ADMIN)
    public ResponseEntity<SellerProfileFullReadDto> approveSellerProfile(@PathVariable Long id) {
        return new ResponseEntity<>(sellerService.approveSellerProfile(id, Status.APPROVED), HttpStatus.OK);
    }

    @PostMapping("/{id}/reject")
    // @PreAuthorize(ADMIN)
    public ResponseEntity<SellerProfileFullReadDto> rejectSellerProfile(@PathVariable Long id) {
        return new ResponseEntity<>(sellerService.rejectSellerProfile(id, Status.REJECTED), HttpStatus.OK);
    }
}
