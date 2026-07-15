package rs.realestate.rental.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.realestate.rental.dto.BookingDTO;
import rs.realestate.rental.dto.BookingRequestDTO;
import rs.realestate.rental.exception.BadRequestException;
import rs.realestate.rental.exception.ResourceNotFoundException;
import rs.realestate.rental.mapper.BookingMapper;
import rs.realestate.rental.model.User;
import rs.realestate.rental.model.Property;
import rs.realestate.rental.model.Booking;
import rs.realestate.rental.model.enums.PropertyStatus;
import rs.realestate.rental.model.enums.BookingStatus;
import rs.realestate.rental.repository.UserRepository;
import rs.realestate.rental.repository.PropertyRepository;
import rs.realestate.rental.repository.BookingRepository;
import rs.realestate.rental.service.PriceCalculator;
import rs.realestate.rental.service.BookingService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PriceCalculator priceCalculator;

    public BookingServiceImpl(BookingRepository bookingRepository,
                             UserRepository userRepository,
                             PropertyRepository propertyRepository,
                             PriceCalculator priceCalculator) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.priceCalculator = priceCalculator;
    }

    @Override
    @Transactional
    public BookingDTO create(BookingRequestDTO dto) {
        if (!dto.getDateTo().isAfter(dto.getDateFrom())) {
            throw new BadRequestException("Kraj perioda mora biti posle pocetka perioda.");
        }
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronadjen: " + dto.getUserId()));
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Nekretnina nije pronadjena: " + dto.getPropertyId()));

        if (property.getStatus() != PropertyStatus.AVAILABLE) {
            throw new BadRequestException("Nekretnina trenutno nije dostupna za zakup.");
        }

        Booking z = new Booking();
        z.setUser(user);
        z.setProperty(property);
        z.setDateFrom(dto.getDateFrom());
        z.setDateTo(dto.getDateTo());
        z.setMessage(dto.getMessage());
        z.setStatus(BookingStatus.SENT);

        Booking saved = bookingRepository.save(z);
        return BookingMapper.toDTO(saved, totalPrice(saved));
    }

    @Override
    public List<BookingDTO> findAll() {
        return bookingRepository.findAll().stream()
                .map(z -> BookingMapper.toDTO(z, totalPrice(z))).collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> byUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(z -> BookingMapper.toDTO(z, totalPrice(z))).collect(Collectors.toList());
    }

    @Override
    public BookingDTO getById(Long id) {
        Booking z = find(id);
        return BookingMapper.toDTO(z, totalPrice(z));
    }

    @Override
    @Transactional
    public BookingDTO changeStatus(Long id, BookingStatus newStatus) {
        Booking z = find(id);
        z.setStatus(newStatus);

        // poslovno pravilo: prihvatanje zahteva izdaje nekretninu
        if (newStatus == BookingStatus.ACCEPTED) {
            Property n = z.getProperty();
            n.setStatus(PropertyStatus.RENTED);
            propertyRepository.save(n);
        }
        Booking saved = bookingRepository.save(z);
        return BookingMapper.toDTO(saved, totalPrice(saved));
    }

    @Override
    @Transactional
    public BookingDTO cancel(Long id, Long userId) {
        Booking z = find(id);
        if (!z.getUser().getId().equals(userId)) {
            throw new BadRequestException("Mozete otkazati samo sopstvene zahteve.");
        }
        if (z.getStatus() != BookingStatus.SENT) {
            throw new BadRequestException("Zahtev se moze otkazati samo dok je u statusu POSLAT.");
        }
        z.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(z);
        return BookingMapper.toDTO(saved, totalPrice(saved));
    }

    // ukupna cena se racuna po danu, uz primenu sezonskog cenovnika ako period pada u sezonu
    private BigDecimal totalPrice(Booking z) {
        return priceCalculator.totalPrice(z.getProperty(), z.getDateFrom(), z.getDateTo());
    }

    private Booking find(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zahtev nije pronadjen: " + id));
    }
}
