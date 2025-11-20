package by.ratingsystem.controller;

import by.ratingsystem.dto.SellerProfileCreateDto;
import by.ratingsystem.dto.SellerProfileReadDto;
import by.ratingsystem.model.Status;
import by.ratingsystem.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<SellerProfileReadDto> createSellerProfile(@RequestBody SellerProfileCreateDto sellerCreateDto) {
        // get authorized userId
        Long userId = 3L;
        return new ResponseEntity<>(sellerService.createSellerProfile(sellerCreateDto, userId), HttpStatus.CREATED);
    }

    @GetMapping
    // @PreAuthorize(ADMIN)
    public ResponseEntity<List<SellerProfileReadDto>> getSellerProfiles(@RequestParam(required = false) Status status) {
        return new ResponseEntity<>(sellerService.findAll(status), HttpStatus.OK);
    }

    @PostMapping("/{id}/approve")
    // @PreAuthorize(ADMIN)
    public ResponseEntity<SellerProfileReadDto> approveSellerProfile(@PathVariable Long id) {
        return new ResponseEntity<>(sellerService.approveSellerProfile(id, Status.APPROVED), HttpStatus.OK);
    }

    @PostMapping("/{id}/reject")
    // @PreAuthorize(ADMIN)
    public ResponseEntity<SellerProfileReadDto> rejectSellerProfile(@PathVariable Long id) {
        return new ResponseEntity<>(sellerService.rejectSellerProfile(id, Status.REJECTED), HttpStatus.OK);
    }

    // TODO update delete
    // check in task
}
