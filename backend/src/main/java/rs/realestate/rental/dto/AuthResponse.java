package rs.realestate.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Odgovor na uspesnu prijavu/registraciju: JWT token + podaci o korisniku.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private UserDTO user;

    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.tokenType = "Bearer";
        this.user = user;
    }
}
