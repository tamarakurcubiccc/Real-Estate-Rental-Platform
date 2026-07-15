package rs.realestate.rental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDTO {

    @NotBlank(message = "Ime je obavezno.")
    private String firstName;

    @NotBlank(message = "Prezime je obavezno.")
    private String lastName;

    @NotBlank(message = "Email je obavezan.")
    @Email(message = "Email nije u ispravnom formatu.")
    private String email;

    @NotBlank(message = "Lozinka je obavezna.")
    @Size(min = 6, message = "Lozinka mora imati najmanje 6 karaktera.")
    private String password;

    private String phone;
}
