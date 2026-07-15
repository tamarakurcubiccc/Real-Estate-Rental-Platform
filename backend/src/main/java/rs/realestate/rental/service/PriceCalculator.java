package rs.realestate.rental.service;

import org.springframework.stereotype.Component;
import rs.realestate.rental.model.Property;
import rs.realestate.rental.model.SeasonalPrice;
import rs.realestate.rental.repository.SeasonalPriceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Obracun ukupne cene zakupa za izabrani period.
 * Osnovna cena nekretnine je mesecna, a za izabrani period se racuna PO DANU:
 * dnevna cena = mesecna / 30. Ako dan pada u definisanu sezonu
 * (sezonski cenovnik), za taj dan se koristi sezonska mesecna cena.
 */
@Component
public class PriceCalculator {

    /** broj dana u mesecu koji se koristi za prevod mesecne cene u dnevnu */
    private static final BigDecimal DAYS_PER_MONTH = BigDecimal.valueOf(30);

    private final SeasonalPriceRepository seasonalPriceRepository;

    public PriceCalculator(SeasonalPriceRepository seasonalPriceRepository) {
        this.seasonalPriceRepository = seasonalPriceRepository;
    }

    public BigDecimal totalPrice(Property property, LocalDate dateFrom, LocalDate dateTo) {
        // broj dana zakupa (ukljucujuci i prvi i poslednji dan perioda)
        long days = ChronoUnit.DAYS.between(dateFrom, dateTo) + 1;
        if (days < 1) days = 1; // minimum jedan dan

        List<SeasonalPrice> seasons =
                seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(property.getId());

        BigDecimal total = BigDecimal.ZERO;
        LocalDate day = dateFrom;
        for (long i = 0; i < days; i++) {
            // mesecna cena koja vazi za taj dan (sezonska ili podrazumevana), pretvorena u dnevnu
            BigDecimal monthly = priceForDay(property, seasons, day);
            BigDecimal daily = monthly.divide(DAYS_PER_MONTH, 4, RoundingMode.HALF_UP);
            total = total.add(daily);
            day = day.plusDays(1);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /** mesecna cena za dati dan: sezonska ako dan pada u sezonu, inace podrazumevana */
    private BigDecimal priceForDay(Property property, List<SeasonalPrice> seasons, LocalDate day) {
        for (SeasonalPrice s : seasons) {
            if (!day.isBefore(s.getStartDate()) && !day.isAfter(s.getEndDate())) {
                return s.getPrice();
            }
        }
        return property.getRentPrice();
    }
}
