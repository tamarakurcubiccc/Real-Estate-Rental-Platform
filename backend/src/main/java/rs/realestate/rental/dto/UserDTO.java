package rs.realestate.rental.dto;

import lombok.Data;
import rs.realestate.rental.model.enums.Role;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role role;
}
