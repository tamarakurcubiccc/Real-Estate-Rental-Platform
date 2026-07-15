package rs.realestate.rental.mapper;

import rs.realestate.rental.dto.ImageDTO;
import rs.realestate.rental.model.Image;

public class ImageMapper {

    public static ImageDTO toDTO(Image s) {
        ImageDTO dto = new ImageDTO();
        dto.setId(s.getId());
        dto.setUrl(s.getUrl());
        dto.setPrimary(s.isPrimary());
        return dto;
    }
}
