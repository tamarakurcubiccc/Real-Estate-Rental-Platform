package rs.realestate.rental.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.realestate.rental.dto.BookingDTO;
import rs.realestate.rental.dto.BookingRequestDTO;
import rs.realestate.rental.model.enums.BookingStatus;
import rs.realestate.rental.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookingDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BookingDTO one(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/user/{userId}")
    public List<BookingDTO> byUser(@PathVariable Long userId) {
        return service.byUser(userId);
    }

    @PostMapping
    public ResponseEntity<BookingDTO> create(@Valid @RequestBody BookingRequestDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    // admin menja status: /api/zahtevi/5/status?status=PRIHVACEN
    @PutMapping("/{id}/status")
    public BookingDTO changeStatus(@PathVariable Long id, @RequestParam BookingStatus status) {
        return service.changeStatus(id, status);
    }

    // korisnik otkazuje sopstveni zahtev: /api/zahtevi/5/otkazi?korisnikId=2
    @PutMapping("/{id}/cancel")
    public BookingDTO cancel(@PathVariable Long id, @RequestParam Long userId) {
        return service.cancel(id, userId);
    }
}
