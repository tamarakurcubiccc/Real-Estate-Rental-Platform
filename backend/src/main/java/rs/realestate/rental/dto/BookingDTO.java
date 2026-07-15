package rs.realestate.rental.dto;

import lombok.Data;
import rs.realestate.rental.model.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingDTO {
    private Long id;
    private LocalDate createdOn;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String message;
    private BookingStatus status;

    private Long userId;
    private String userFullName;

    private Long propertyId;
    private String propertyName;

    // izracunata ukupna cena za izabrani period
    private BigDecimal totalPrice;
}
