package rs.realestate.rental.service.impl;

import org.springframework.stereotype.Service;
import rs.realestate.rental.dto.StatisticsDTO;
import rs.realestate.rental.model.Booking;
import rs.realestate.rental.model.Property;
import rs.realestate.rental.model.Review;
import rs.realestate.rental.model.enums.BookingStatus;
import rs.realestate.rental.model.enums.PropertyStatus;
import rs.realestate.rental.repository.BookingRepository;
import rs.realestate.rental.repository.PropertyRepository;
import rs.realestate.rental.repository.ReviewRepository;
import rs.realestate.rental.repository.UserRepository;
import rs.realestate.rental.service.StatisticsService;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public StatisticsServiceImpl(PropertyRepository propertyRepository,
                                 BookingRepository bookingRepository,
                                 ReviewRepository reviewRepository,
                                 UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    public StatisticsDTO statistics() {
        List<Property> properties = propertyRepository.findAll();
        List<Booking> bookings = bookingRepository.findAll();

        StatisticsDTO dto = new StatisticsDTO();
        dto.setTotalProperties(properties.size());
        dto.setTotalBookings(bookings.size());
        dto.setTotalUsers(userRepository.count());

        // nekretnine po statusu (svi statusi prikazani, makar i 0)
        Map<String, Long> byStatusProps = new LinkedHashMap<>();
        for (PropertyStatus s : PropertyStatus.values()) {
            byStatusProps.put(s.name(), properties.stream().filter(p -> p.getStatus() == s).count());
        }
        dto.setPropertiesByStatus(byStatusProps);

        // zahtevi po statusu
        Map<String, Long> byStatusBookings = new LinkedHashMap<>();
        for (BookingStatus s : BookingStatus.values()) {
            byStatusBookings.put(s.name(), bookings.stream().filter(b -> b.getStatus() == s).count());
        }
        dto.setBookingsByStatus(byStatusBookings);

        // popunjenost = izdate / ukupno * 100
        long rented = byStatusProps.getOrDefault(PropertyStatus.RENTED.name(), 0L);
        double occupancy = properties.isEmpty() ? 0.0 : (rented * 100.0) / properties.size();
        dto.setOccupancyPercent(Math.round(occupancy * 10.0) / 10.0);

        // najtrazenije nekretnine (po broju zahteva)
        Map<Long, Long> bookingsPerProperty = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getProperty().getId(), Collectors.counting()));
        List<StatisticsDTO.MostRequestedDTO> mostRequested = properties.stream()
                .map(p -> new StatisticsDTO.MostRequestedDTO(
                        p.getId(), p.getName(), bookingsPerProperty.getOrDefault(p.getId(), 0L)))
                .sorted(Comparator.comparingLong(StatisticsDTO.MostRequestedDTO::getBookingCount).reversed())
                .collect(Collectors.toList());
        dto.setMostRequested(mostRequested);

        // prosecne ocene po nekretnini (odobrene recenzije)
        List<StatisticsDTO.PropertyRatingDTO> ratings = properties.stream()
                .map(p -> {
                    List<Review> approved = reviewRepository.findByPropertyIdAndApprovedTrue(p.getId());
                    double average = approved.isEmpty() ? 0.0 :
                            approved.stream().mapToInt(Review::getRating).average().orElse(0.0);
                    return new StatisticsDTO.PropertyRatingDTO(
                            p.getId(), p.getName(), Math.round(average * 10.0) / 10.0, approved.size());
                })
                .sorted(Comparator.comparingDouble(StatisticsDTO.PropertyRatingDTO::getAverageRating).reversed())
                .collect(Collectors.toList());
        dto.setAverageRatings(ratings);

        return dto;
    }
}
