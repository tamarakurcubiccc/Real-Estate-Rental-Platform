package rs.realestate.rental.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageDTO {
    private Long id;

    @NotBlank(message = "URL slike je obavezan.")
    private String url;

    private boolean primary;
}
