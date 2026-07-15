package rs.realestate.rental.mapper;

import rs.realestate.rental.dto.ReviewDTO;
import rs.realestate.rental.model.Review;

public class ReviewMapper {

    public static ReviewDTO toDTO(Review r) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(r.getId());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setDate(r.getDate());
        dto.setApproved(r.isApproved());
        dto.setUserId(r.getUser().getId());
        dto.setUserFullName(r.getUser().getFirstName() + " " + r.getUser().getLastName());
        dto.setPropertyId(r.getProperty().getId());
        dto.setPropertyName(r.getProperty().getName());
        return dto;
    }
}
