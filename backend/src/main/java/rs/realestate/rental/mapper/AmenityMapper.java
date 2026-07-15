package rs.realestate.rental.mapper;

import rs.realestate.rental.dto.AmenityDTO;
import rs.realestate.rental.model.Amenity;

public class AmenityMapper {

    public static AmenityDTO toDTO(Amenity p) {
        AmenityDTO dto = new AmenityDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        return dto;
    }
}
