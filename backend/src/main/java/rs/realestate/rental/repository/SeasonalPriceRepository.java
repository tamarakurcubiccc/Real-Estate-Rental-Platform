package rs.realestate.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.realestate.rental.model.SeasonalPrice;

import java.util.List;

public interface SeasonalPriceRepository extends JpaRepository<SeasonalPrice, Long> {
    List<SeasonalPrice> findByPropertyIdOrderByStartDateAsc(Long propertyId);
    void deleteByPropertyId(Long propertyId);
}
