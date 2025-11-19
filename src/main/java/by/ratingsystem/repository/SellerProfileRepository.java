package by.ratingsystem.repository;

import by.ratingsystem.model.SellerProfile;
import by.ratingsystem.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    List<SellerProfile> findAllByStatus(Status status);
}
