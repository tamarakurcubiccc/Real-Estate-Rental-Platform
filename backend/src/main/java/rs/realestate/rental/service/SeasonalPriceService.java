package rs.realestate.rental.service;

import rs.realestate.rental.dto.SeasonalPriceDTO;
import rs.realestate.rental.dto.SeasonalPriceRequestDTO;

import java.util.List;

public interface SeasonalPriceService {
    List<SeasonalPriceDTO> forProperty(Long propertyId);
    SeasonalPriceDTO add(Long propertyId, SeasonalPriceRequestDTO dto);
    SeasonalPriceDTO update(Long propertyId, Long seasonalPriceId, SeasonalPriceRequestDTO dto);
    void delete(Long propertyId, Long seasonalPriceId);
}
