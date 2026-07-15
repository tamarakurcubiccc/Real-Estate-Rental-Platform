package rs.realestate.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.realestate.rental.dto.SeasonalPriceDTO;
import rs.realestate.rental.dto.SeasonalPriceRequestDTO;
import rs.realestate.rental.service.SeasonalPriceService;

import java.util.List;

@RestController
@RequestMapping("/api/properties/{propertyId}/seasonal-prices")
@Tag(name = "Sezonski cenovnik", description = "Sezonske cene zakupa po nekretnini")
public class SeasonalPriceController {

    private final SeasonalPriceService service;

    public SeasonalPriceController(SeasonalPriceService service) {
        this.service = service;
    }

    @Operation(summary = "Sezone za nekretninu", description = "Javno dostupno.")
    @GetMapping
    public List<SeasonalPriceDTO> forProperty(@PathVariable Long propertyId) {
        return service.forProperty(propertyId);
    }

    @Operation(summary = "Dodaj sezonu (ADMIN)")
    @PostMapping
    public ResponseEntity<SeasonalPriceDTO> add(@PathVariable Long propertyId,
                                                     @Valid @RequestBody SeasonalPriceRequestDTO dto) {
        return new ResponseEntity<>(service.add(propertyId, dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Izmeni sezonu (ADMIN)")
    @PutMapping("/{seasonalPriceId}")
    public SeasonalPriceDTO update(@PathVariable Long propertyId,
                                      @PathVariable Long seasonalPriceId,
                                      @Valid @RequestBody SeasonalPriceRequestDTO dto) {
        return service.update(propertyId, seasonalPriceId, dto);
    }

    @Operation(summary = "Obrisi sezonu (ADMIN)")
    @DeleteMapping("/{seasonalPriceId}")
    public ResponseEntity<Void> delete(@PathVariable Long propertyId, @PathVariable Long seasonalPriceId) {
        service.delete(propertyId, seasonalPriceId);
        return ResponseEntity.noContent().build();
    }
}
