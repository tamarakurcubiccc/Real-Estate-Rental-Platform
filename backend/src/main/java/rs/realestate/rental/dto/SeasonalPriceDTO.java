package rs.realestate.rental.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SeasonalPriceDTO {
    private Long id;
    private Long propertyId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
}
