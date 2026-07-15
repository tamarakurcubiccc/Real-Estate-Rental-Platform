package rs.realestate.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.realestate.rental.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPropertyIdAndApprovedTrue(Long propertyId);
    List<Review> findByPropertyId(Long propertyId);
    List<Review> findByApprovedFalse();
}
