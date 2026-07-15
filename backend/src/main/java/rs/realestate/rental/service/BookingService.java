package rs.realestate.rental.service;

import rs.realestate.rental.dto.BookingDTO;
import rs.realestate.rental.dto.BookingRequestDTO;
import rs.realestate.rental.model.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingDTO create(BookingRequestDTO dto);
    List<BookingDTO> findAll();
    List<BookingDTO> byUser(Long userId);
    BookingDTO getById(Long id);
    BookingDTO changeStatus(Long id, BookingStatus newStatus);
    BookingDTO cancel(Long id, Long userId);
}
