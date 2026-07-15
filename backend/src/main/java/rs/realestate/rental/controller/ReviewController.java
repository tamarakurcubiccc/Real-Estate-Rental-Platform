package rs.realestate.rental.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.realestate.rental.dto.ReviewDTO;
import rs.realestate.rental.dto.ReviewRequestDTO;
import rs.realestate.rental.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    // odobrene recenzije za nekretninu (vidljive svima)
    @GetMapping("/property/{propertyId}")
    public List<ReviewDTO> forProperty(@PathVariable Long propertyId) {
        return service.approvedForProperty(propertyId);
    }

    // recenzije koje cekaju moderaciju (admin)
    @GetMapping("/pending")
    public List<ReviewDTO> pending() {
        return service.pending();
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> create(@Valid @RequestBody ReviewRequestDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/approve")
    public ReviewDTO approve(@PathVariable Long id) {
        return service.approve(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
