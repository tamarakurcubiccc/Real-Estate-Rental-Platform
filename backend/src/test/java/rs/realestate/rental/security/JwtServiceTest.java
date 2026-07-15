package rs.realestate.rental.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testovi za generisanje i validaciju JWT tokena.
 */
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "test-tajni-kljuc-za-potpisivanje-JWT-tokena-1234567890",
                3600000L);
    }

    @Test
    void generateToken_thenExtractEmailAndRole() {
        String token = jwtService.generateToken("admin@nekretnine.rs", "ADMIN");

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractEmail(token)).isEqualTo("admin@nekretnine.rs");
        assertThat(jwtService.extractRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void isTokenValid_forSameUser_returnsTrue() {
        String token = jwtService.generateToken("pera@example.com", "USER");
        UserDetails user = new User("pera@example.com", "x",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValid_forDifferentUser_returnsFalse() {
        String token = jwtService.generateToken("pera@example.com", "USER");
        UserDetails other = new User("mika@example.com", "x",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        assertThat(jwtService.isTokenValid(token, other)).isFalse();
    }

    @Test
    void expiredToken_throwsException() {
        JwtService expired = new JwtService(
                "test-tajni-kljuc-za-potpisivanje-JWT-tokena-1234567890",
                -1000L); // vec istekao
        String token = expired.generateToken("x@x.com", "USER");

        // parsiranje isteklog tokena baca io.jsonwebtoken.ExpiredJwtException
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> expired.extractEmail(token))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }
}
