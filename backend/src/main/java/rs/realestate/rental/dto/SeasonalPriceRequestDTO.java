package rs.realestate.rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SeasonalPriceRequestDTO {

    @NotBlank(message = "Naziv sezone je obavezan.")
    private String name;

    @NotNull(message = "Pocetak sezone je obavezan.")
    private LocalDate startDate;

    @NotNull(message = "Kraj sezone je obavezan.")
    private LocalDate endDate;

    @NotNull(message = "Cena je obavezna.")
    @Positive(message = "Cena mora biti veca od nule.")
    private BigDecimal price;
}
