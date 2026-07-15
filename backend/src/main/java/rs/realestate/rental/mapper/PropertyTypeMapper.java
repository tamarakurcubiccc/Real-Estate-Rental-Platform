package rs.realestate.rental.mapper;

import rs.realestate.rental.dto.PropertyTypeDTO;
import rs.realestate.rental.model.PropertyType;

public class PropertyTypeMapper {

    public static PropertyTypeDTO toDTO(PropertyType t) {
        if (t == null) return null;
        PropertyTypeDTO dto = new PropertyTypeDTO();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setDescription(t.getDescription());
        return dto;
    }

    public static PropertyType toEntity(PropertyTypeDTO dto) {
        PropertyType t = new PropertyType();
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        return t;
    }
}
