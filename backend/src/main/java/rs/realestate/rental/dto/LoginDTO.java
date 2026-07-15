package rs.realestate.rental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "Email je obavezan.")
    @Email
    private String email;

    @NotBlank(message = "Lozinka je obavezna.")
    private String password;
}
