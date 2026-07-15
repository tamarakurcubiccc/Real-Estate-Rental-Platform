package rs.realestate.rental.dto;

import lombok.Data;
import rs.realestate.rental.model.enums.PropertyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class PropertyDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private double area;
    private int rooms;
    private int floor;
    private BigDecimal rentPrice;
    private BigDecimal deposit;
    private String description;
    private PropertyStatus status;
    private LocalDate publishedOn;

    private PropertyTypeDTO type;
    private Set<AmenityDTO> amenities;
    private List<ImageDTO> images;

    private double averageRating;
    private int reviewCount;
}
