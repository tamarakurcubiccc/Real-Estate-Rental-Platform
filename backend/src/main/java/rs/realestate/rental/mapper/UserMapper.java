package rs.realestate.rental.mapper;

import rs.realestate.rental.dto.UserDTO;
import rs.realestate.rental.model.User;

public class UserMapper {

    public static UserDTO toDTO(User k) {
        UserDTO dto = new UserDTO();
        dto.setId(k.getId());
        dto.setFirstName(k.getFirstName());
        dto.setLastName(k.getLastName());
        dto.setEmail(k.getEmail());
        dto.setPhone(k.getPhone());
        dto.setRole(k.getRole());
        return dto;
    }
}
