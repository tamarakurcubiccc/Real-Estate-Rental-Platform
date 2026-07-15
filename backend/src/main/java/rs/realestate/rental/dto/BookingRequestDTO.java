package rs.realestate.rental.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Korisnik je obavezan.")
    private Long userId;

    @NotNull(message = "Nekretnina je obavezna.")
    private Long propertyId;

    @NotNull(message = "Pocetak perioda je obavezan.")
    @jakarta.validation.constraints.FutureOrPresent(message = "Period ne moze biti u proslosti.")
    private LocalDate dateFrom;

    @NotNull(message = "Kraj perioda je obavezan.")
    private LocalDate dateTo;

    private String message;
}
