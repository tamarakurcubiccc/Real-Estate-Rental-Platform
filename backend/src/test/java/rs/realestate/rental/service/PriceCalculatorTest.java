package rs.realestate.rental.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.realestate.rental.model.Property;
import rs.realestate.rental.model.SeasonalPrice;
import rs.realestate.rental.repository.SeasonalPriceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Jedinicni testovi obracuna cene zakupa sa sezonskim cenovnikom.
 * Dnevna cena = mesecna / 30.
 */
@ExtendWith(MockitoExtension.class)
class PriceCalculatorTest {

    @Mock private SeasonalPriceRepository seasonalPriceRepository;
    @InjectMocks private PriceCalculator calculator;

    private Property property(String monthlyPrice) {
        Property n = new Property();
        n.setId(1L);
        n.setRentPrice(new BigDecimal(monthlyPrice));
        return n;
    }

    private SeasonalPrice season(String name, LocalDate od, LocalDate endDate, String price) {
        SeasonalPrice s = new SeasonalPrice();
        s.setName(name);
        s.setStartDate(od);
        s.setEndDate(endDate);
        s.setPrice(new BigDecimal(price));
        return s;
    }

    @Test
    void withoutSeasons_usesDefaultPrice() {
        Property n = property("600"); // 600/30 = 20 € dnevno
        when(seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(anyLong()))
                .thenReturn(Collections.emptyList());

        // 10 dana (1. do 10. ukljucivo) * 20 = 200
        BigDecimal price = calculator.totalPrice(n,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 10));

        assertThat(price).isEqualByComparingTo("200.00");
    }

    @Test
    void sameDay_chargesOneDay() {
        Property n = property("600");
        when(seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(anyLong()))
                .thenReturn(Collections.emptyList());

        BigDecimal price = calculator.totalPrice(n,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1));

        assertThat(price).isEqualByComparingTo("20.00");
    }

    @Test
    void periodInSeason_usesSeasonalPrice() {
        Property n = property("600"); // van sezone 20 €/dan
        // letnja sezona: 900/30 = 30 €/dan
        when(seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(anyLong()))
                .thenReturn(List.of(season("Letnja", LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 8, 31), "900")));

        // 5 dana u sezoni * 30 = 150
        BigDecimal price = calculator.totalPrice(n,
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 14));

        assertThat(price).isEqualByComparingTo("150.00");
    }

    @Test
    void periodOverlapsSeason_combinesPrices() {
        Property n = property("600"); // van sezone 20 €/dan
        when(seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(anyLong()))
                .thenReturn(List.of(season("Letnja", LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 8, 31), "900"))); // u sezoni 30 €/dan

        // 29. i 30. jun van sezone (2 * 20 = 40), 1. i 2. jul u sezoni (2 * 30 = 60) => 100
        BigDecimal price = calculator.totalPrice(n,
                LocalDate.of(2026, 6, 29), LocalDate.of(2026, 7, 2));

        assertThat(price).isEqualByComparingTo("100.00");
    }
}
