package rs.realestate.rental.mapper;

import rs.realestate.rental.dto.BookingDTO;
import rs.realestate.rental.model.Booking;

import java.math.BigDecimal;

public class BookingMapper {

    public static BookingDTO toDTO(Booking z, BigDecimal totalPrice) {
        BookingDTO dto = new BookingDTO();
        dto.setId(z.getId());
        dto.setCreatedOn(z.getCreatedOn());
        dto.setDateFrom(z.getDateFrom());
        dto.setDateTo(z.getDateTo());
        dto.setMessage(z.getMessage());
        dto.setStatus(z.getStatus());
        dto.setUserId(z.getUser().getId());
        dto.setUserFullName(z.getUser().getFirstName() + " " + z.getUser().getLastName());
        dto.setPropertyId(z.getProperty().getId());
        dto.setPropertyName(z.getProperty().getName());
        dto.setTotalPrice(totalPrice);
        return dto;
    }
}
