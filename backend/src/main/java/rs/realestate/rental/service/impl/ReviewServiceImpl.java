package rs.realestate.rental.service.impl;

import org.springframework.stereotype.Service;
import rs.realestate.rental.dto.ReviewDTO;
import rs.realestate.rental.dto.ReviewRequestDTO;
import rs.realestate.rental.exception.ResourceNotFoundException;
import rs.realestate.rental.mapper.ReviewMapper;
import rs.realestate.rental.model.User;
import rs.realestate.rental.model.Property;
import rs.realestate.rental.model.Review;
import rs.realestate.rental.repository.UserRepository;
import rs.realestate.rental.repository.PropertyRepository;
import rs.realestate.rental.repository.ReviewRepository;
import rs.realestate.rental.service.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                                UserRepository userRepository,
                                PropertyRepository propertyRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public ReviewDTO create(ReviewRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronadjen: " + dto.getUserId()));
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Nekretnina nije pronadjena: " + dto.getPropertyId()));

        Review r = new Review();
        r.setUser(user);
        r.setProperty(property);
        r.setRating(dto.getRating());
        r.setComment(dto.getComment());
        r.setApproved(false); // ceka moderaciju administratora
        return ReviewMapper.toDTO(reviewRepository.save(r));
    }

    @Override
    public List<ReviewDTO> approvedForProperty(Long propertyId) {
        return reviewRepository.findByPropertyIdAndApprovedTrue(propertyId).stream()
                .map(ReviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> pending() {
        return reviewRepository.findByApprovedFalse().stream()
                .map(ReviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ReviewDTO approve(Long id) {
        Review r = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recenzija nije pronadjena: " + id));
        r.setApproved(true);
        return ReviewMapper.toDTO(reviewRepository.save(r));
    }

    @Override
    public void delete(Long id) {
        Review r = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recenzija nije pronadjena: " + id));
        reviewRepository.delete(r);
    }
}
