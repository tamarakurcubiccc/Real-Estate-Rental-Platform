package rs.realestate.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.realestate.rental.model.PropertyType;

public interface PropertyTypeRepository extends JpaRepository<PropertyType, Long> {
    boolean existsByName(String name);
}
