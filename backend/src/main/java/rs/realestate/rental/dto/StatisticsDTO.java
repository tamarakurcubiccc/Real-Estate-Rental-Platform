package rs.realestate.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Zbirni statisticki podaci za administratorski pregled (osnova za grafikone):
 * ukupni brojevi, raspodela po statusima, najtrazenije nekretnine i prosecne ocene.
 */
@Data
public class StatisticsDTO {

    private long totalProperties;
    private long totalBookings;
    private long totalUsers;

    // broj nekretnina po statusu (AVAILABLE, RESERVED, RENTED, WITHDRAWN)
    private Map<String, Long> propertiesByStatus;

    // broj zahteva po statusu (SENT, ACCEPTED, REJECTED, CANCELLED)
    private Map<String, Long> bookingsByStatus;

    // popunjenost = procenat izdatih nekretnina u ukupnoj ponudi
    private double occupancyPercent;

    // najtrazenije nekretnine (po broju zahteva, opadajuce)
    private List<MostRequestedDTO> mostRequested;

    // prosecne ocene po nekretnini (po odobrenim recenzijama)
    private List<PropertyRatingDTO> averageRatings;

    @Data
    @AllArgsConstructor
    public static class MostRequestedDTO {
        private Long propertyId;
        private String name;
        private long bookingCount;
    }

    @Data
    @AllArgsConstructor
    public static class PropertyRatingDTO {
        private Long propertyId;
        private String name;
        private double averageRating;
        private int reviewCount;
    }
}
