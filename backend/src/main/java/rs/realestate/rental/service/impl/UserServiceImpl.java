package rs.realestate.rental.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.realestate.rental.dto.AuthResponse;
import rs.realestate.rental.dto.UserDTO;
import rs.realestate.rental.dto.LoginDTO;
import rs.realestate.rental.dto.RegistrationDTO;
import rs.realestate.rental.exception.BadRequestException;
import rs.realestate.rental.exception.ResourceNotFoundException;
import rs.realestate.rental.mapper.UserMapper;
import rs.realestate.rental.model.User;
import rs.realestate.rental.model.enums.Role;
import rs.realestate.rental.repository.UserRepository;
import rs.realestate.rental.security.JwtService;
import rs.realestate.rental.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               AuthenticationManager authenticationManager,
                               JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Korisnik sa ovim email-om vec postoji.");
        }
        User k = new User();
        k.setFirstName(dto.getFirstName());
        k.setLastName(dto.getLastName());
        k.setEmail(dto.getEmail());
        k.setPassword(passwordEncoder.encode(dto.getPassword())); // BCrypt hesiranje
        k.setPhone(dto.getPhone());
        k.setRole(Role.USER);
        User saved = userRepository.save(k);

        String token = jwtService.generateToken(saved.getEmail(), saved.getRole().name());
        return new AuthResponse(token, UserMapper.toDTO(saved));
    }

    @Override
    public AuthResponse login(LoginDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Pogresan email ili lozinka.");
        }

        User k = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadRequestException("Pogresan email ili lozinka."));

        String token = jwtService.generateToken(k.getEmail(), k.getRole().name());
        return new AuthResponse(token, UserMapper.toDTO(k));
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getById(Long id) {
        User k = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronadjen: " + id));
        return UserMapper.toDTO(k);
    }
}
