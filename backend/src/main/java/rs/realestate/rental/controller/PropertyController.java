package rs.realestate.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.realestate.rental.dto.PropertyDTO;
import rs.realestate.rental.dto.PropertyRequestDTO;
import rs.realestate.rental.dto.ImageDTO;
import rs.realestate.rental.model.enums.PropertyStatus;
import rs.realestate.rental.service.PropertyService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    /** podrazumevani broj nekretnina po strani */
    private static final int DEFAULT_SIZE = 3;
    private static final int MAX_SIZE = 50;

    private final PropertyService service;

    public PropertyController(PropertyService service) {
        this.service = service;
    }

    // GET sa opcionim filterima i paginacijom:
    // /api/nekretnine?grad=Beograd&tipId=1&minCena=300&maxCena=800&brojSoba=2&status=DOSTUPNA&page=0&size=3
    @Operation(summary = "Pretraga nekretnina (paginirano)",
            description = "Dinamicko filtriranje uz paginaciju. Podrazumevano 3 nekretnine po strani, "
                    + "sortirano od najnovije dodate.")
    @GetMapping
    public Page<PropertyDTO> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer rooms,
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        int pageNumber = Math.max(page, 0);
        int pageSize = (size != null && size >= 1 && size <= MAX_SIZE) ? size : DEFAULT_SIZE;
        // sort opadajuce po id-u: najnovije dodate nekretnine na prvoj strani
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        return service.search(city, typeId, minPrice, maxPrice, rooms, status, pageable);
    }

    @GetMapping("/{id}")
    public PropertyDTO one(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<PropertyDTO> create(@Valid @RequestBody PropertyRequestDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public PropertyDTO update(@PathVariable Long id, @Valid @RequestBody PropertyRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // galerija slika
    @PostMapping("/{id}/images")
    public PropertyDTO addImage(@PathVariable Long id, @Valid @RequestBody ImageDTO imageDTO) {
        return service.addImage(id, imageDTO);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id, @PathVariable Long imageId) {
        service.deleteImage(id, imageId);
        return ResponseEntity.noContent().build();
    }
}
