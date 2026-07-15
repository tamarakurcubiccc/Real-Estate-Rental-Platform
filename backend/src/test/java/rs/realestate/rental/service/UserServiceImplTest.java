package rs.realestate.rental.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.realestate.rental.dto.AuthResponse;
import rs.realestate.rental.dto.LoginDTO;
import rs.realestate.rental.dto.RegistrationDTO;
import rs.realestate.rental.exception.BadRequestException;
import rs.realestate.rental.model.User;
import rs.realestate.rental.model.enums.Role;
import rs.realestate.rental.repository.UserRepository;
import rs.realestate.rental.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Jedinicni (unit) testovi servisa za korisnike - koriste Mockito za mokovanje zavisnosti.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private rs.realestate.rental.security.JwtService jwtService;

    @InjectMocks private UserServiceImpl service;

    private RegistrationDTO validRegistration() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setFirstName("Mika");
        dto.setLastName("Mikic");
        dto.setEmail("mika@example.com");
        dto.setPassword("tajna123");
        dto.setPhone("0641234567");
        return dto;
    }

    @Test
    void register_success_hashesPasswordAndReturnsToken() {
        RegistrationDTO dto = validRegistration();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("tajna123")).thenReturn("$2a$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User k = inv.getArgument(0);
            k.setId(1L);
            return k;
        });
        when(jwtService.generateToken(eq("mika@example.com"), anyString())).thenReturn("jwt.token.value");

        AuthResponse response = service.register(dto);

        assertThat(response.getToken()).isEqualTo("jwt.token.value");
        assertThat(response.getUser().getEmail()).isEqualTo("mika@example.com");
        assertThat(response.getUser().getRole()).isEqualTo(Role.USER);

        // lozinka mora biti sacuvana hesirana, ne u plain-textu
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("$2a$hashed");
        assertThat(captor.getValue().getPassword()).isNotEqualTo("tajna123");
    }

    @Test
    void register_existingEmail_throwsBadRequest() {
        RegistrationDTO dto = validRegistration();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.register(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("vec postoji");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_success_returnsToken() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("pera@example.com");
        dto.setPassword("pera123");

        User k = new User();
        k.setId(2L);
        k.setEmail("pera@example.com");
        k.setRole(Role.USER);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // uspesna autentikacija
        when(userRepository.findByEmail("pera@example.com")).thenReturn(Optional.of(k));
        when(jwtService.generateToken("pera@example.com", "USER")).thenReturn("jwt.pera");

        AuthResponse response = service.login(dto);

        assertThat(response.getToken()).isEqualTo("jwt.pera");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void login_wrongPassword_throwsBadRequest() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("pera@example.com");
        dto.setPassword("pogresna");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> service.login(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Pogresan email ili lozinka");
    }
}
