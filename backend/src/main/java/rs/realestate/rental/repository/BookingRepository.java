package rs.realestate.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.realestate.rental.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByPropertyId(Long propertyId);
}
