package rs.realestate.rental.mapper;

import rs.realestate.rental.dto.PropertyDTO;
import rs.realestate.rental.dto.AmenityDTO;
import rs.realestate.rental.dto.ImageDTO;
import rs.realestate.rental.model.Property;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertyMapper {

    public static PropertyDTO toDTO(Property n, double averageRating, int reviewCount) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(n.getId());
        dto.setName(n.getName());
        dto.setAddress(n.getAddress());
        dto.setCity(n.getCity());
        dto.setArea(n.getArea());
        dto.setRooms(n.getRooms());
        dto.setFloor(n.getFloor());
        dto.setRentPrice(n.getRentPrice());
        dto.setDeposit(n.getDeposit());
        dto.setDescription(n.getDescription());
        dto.setStatus(n.getStatus());
        dto.setPublishedOn(n.getPublishedOn());
        dto.setType(PropertyTypeMapper.toDTO(n.getType()));

        Set<AmenityDTO> amenities = n.getAmenities().stream()
                .map(AmenityMapper::toDTO)
                .collect(Collectors.toSet());
        dto.setAmenities(amenities);

        List<ImageDTO> images = n.getImages().stream()
                .map(ImageMapper::toDTO)
                .collect(Collectors.toList());
        dto.setImages(images);

        dto.setAverageRating(averageRating);
        dto.setReviewCount(reviewCount);
        return dto;
    }
}
