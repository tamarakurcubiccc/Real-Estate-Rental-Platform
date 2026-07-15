package rs.realestate.rental.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequestDTO {

    @NotNull(message = "Korisnik je obavezan.")
    private Long userId;

    @NotNull(message = "Nekretnina je obavezna.")
    private Long propertyId;

    @NotNull(message = "Ocena je obavezna.")
    @Min(value = 1, message = "Ocena mora biti izmedju 1 i 5.")
    @Max(value = 5, message = "Ocena mora biti izmedju 1 i 5.")
    private Integer rating;

    private String comment;
}
