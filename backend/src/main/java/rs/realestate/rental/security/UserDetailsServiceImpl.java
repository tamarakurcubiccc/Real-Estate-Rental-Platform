package rs.realestate.rental.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.realestate.rental.model.User;
import rs.realestate.rental.repository.UserRepository;

import java.util.List;

/**
 * Ucitava korisnika iz baze po email-u i mapira ga u Spring Security UserDetails.
 * Uloga se prevodi u autoritet oblika ROLE_ADMIN / ROLE_USER.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronadjen: " + email));

        // puno kvalifikovano ime: Spring-ov User se sudara sa nasim entitetom User
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
