package rs.realestate.rental.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.realestate.rental.dto.PropertyTypeDTO;
import rs.realestate.rental.service.PropertyTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/property-types")
public class PropertyTypeController {

    private final PropertyTypeService service;

    public PropertyTypeController(PropertyTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<PropertyTypeDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PropertyTypeDTO one(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<PropertyTypeDTO> create(@Valid @RequestBody PropertyTypeDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public PropertyTypeDTO update(@PathVariable Long id, @Valid @RequestBody PropertyTypeDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
