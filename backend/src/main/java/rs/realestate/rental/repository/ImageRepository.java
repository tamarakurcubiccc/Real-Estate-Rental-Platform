package rs.realestate.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.realestate.rental.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
