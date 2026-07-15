package rs.realestate.rental.mapper;

import rs.realestate.rental.dto.SeasonalPriceDTO;
import rs.realestate.rental.model.SeasonalPrice;

public class SeasonalPriceMapper {

    public static SeasonalPriceDTO toDTO(SeasonalPrice s) {
        if (s == null) return null;
        SeasonalPriceDTO dto = new SeasonalPriceDTO();
        dto.setId(s.getId());
        dto.setPropertyId(s.getProperty().getId());
        dto.setName(s.getName());
        dto.setStartDate(s.getStartDate());
        dto.setEndDate(s.getEndDate());
        dto.setPrice(s.getPrice());
        return dto;
    }
}
