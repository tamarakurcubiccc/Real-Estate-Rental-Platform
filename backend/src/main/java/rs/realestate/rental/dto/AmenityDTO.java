package rs.realestate.rental.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AmenityDTO {
    private Long id;

    @NotBlank(message = "Naziv pogodnosti je obavezan.")
    private String name;
}
