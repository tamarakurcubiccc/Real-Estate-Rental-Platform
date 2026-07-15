package rs.realestate.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.realestate.rental.model.Amenity;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    boolean existsByName(String name);
}
