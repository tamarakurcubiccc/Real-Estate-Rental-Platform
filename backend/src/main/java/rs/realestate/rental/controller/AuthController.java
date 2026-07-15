package rs.realestate.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.realestate.rental.dto.AuthResponse;
import rs.realestate.rental.dto.LoginDTO;
import rs.realestate.rental.dto.RegistrationDTO;
import rs.realestate.rental.service.UserService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autentikacija", description = "Registracija i prijava korisnika (JWT)")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registracija novog korisnika", description = "Kreira nalog i vraca JWT token.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationDTO dto) {
        return new ResponseEntity<>(userService.register(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Prijava korisnika", description = "Proverava kredencijale i vraca JWT token.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(userService.login(dto));
    }
}
