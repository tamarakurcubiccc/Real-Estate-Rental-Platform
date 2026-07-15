package rs.realestate.rental.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PropertyTypeDTO {
    private Long id;

    @NotBlank(message = "Naziv tipa je obavezan.")
    private String name;

    private String description;
}
