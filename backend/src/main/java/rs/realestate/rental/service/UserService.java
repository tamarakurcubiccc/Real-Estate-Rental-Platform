package rs.realestate.rental.service;

import rs.realestate.rental.dto.AuthResponse;
import rs.realestate.rental.dto.UserDTO;
import rs.realestate.rental.dto.LoginDTO;
import rs.realestate.rental.dto.RegistrationDTO;

import java.util.List;

public interface UserService {
    AuthResponse register(RegistrationDTO dto);
    AuthResponse login(LoginDTO dto);
    List<UserDTO> findAllUsers();
    UserDTO getById(Long id);
}
