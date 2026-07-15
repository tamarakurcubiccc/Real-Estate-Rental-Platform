package rs.realestate.rental.service;

import rs.realestate.rental.dto.ReviewDTO;
import rs.realestate.rental.dto.ReviewRequestDTO;

import java.util.List;

public interface ReviewService {
    ReviewDTO create(ReviewRequestDTO dto);
    List<ReviewDTO> approvedForProperty(Long propertyId);
    List<ReviewDTO> pending();
    ReviewDTO approve(Long id);
    void delete(Long id);
}
