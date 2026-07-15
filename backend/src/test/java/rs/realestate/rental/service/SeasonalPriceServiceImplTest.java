package rs.realestate.rental.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.realestate.rental.dto.SeasonalPriceDTO;
import rs.realestate.rental.dto.SeasonalPriceRequestDTO;
import rs.realestate.rental.exception.BadRequestException;
import rs.realestate.rental.model.Property;
import rs.realestate.rental.model.SeasonalPrice;
import rs.realestate.rental.repository.PropertyRepository;
import rs.realestate.rental.repository.SeasonalPriceRepository;
import rs.realestate.rental.service.impl.SeasonalPriceServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Jedinicni testovi poslovnih pravila sezonskog cenovnika
 * (validacija datuma i zabrana preklapanja sezona).
 */
@ExtendWith(MockitoExtension.class)
class SeasonalPriceServiceImplTest {

    @Mock private SeasonalPriceRepository seasonalPriceRepository;
    @Mock private PropertyRepository propertyRepository;
    @InjectMocks private SeasonalPriceServiceImpl service;

    private SeasonalPriceRequestDTO booking(LocalDate od, LocalDate endDate) {
        SeasonalPriceRequestDTO dto = new SeasonalPriceRequestDTO();
        dto.setName("Letnja sezona");
        dto.setStartDate(od);
        dto.setEndDate(endDate);
        dto.setPrice(new BigDecimal("900"));
        return dto;
    }

    private SeasonalPrice existing(Long id, LocalDate od, LocalDate endDate) {
        Property n = new Property();
        n.setId(1L);
        SeasonalPrice s = new SeasonalPrice();
        s.setId(id);
        s.setProperty(n);
        s.setName("Postojeca");
        s.setStartDate(od);
        s.setEndDate(endDate);
        s.setPrice(new BigDecimal("800"));
        return s;
    }

    @Test
    void add_withoutOverlap_succeeds() {
        Property n = new Property();
        n.setId(1L);
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(n));
        when(seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(1L))
                .thenReturn(Collections.emptyList());
        when(seasonalPriceRepository.save(any(SeasonalPrice.class))).thenAnswer(inv -> {
            SeasonalPrice s = inv.getArgument(0);
            s.setId(5L);
            return s;
        });

        SeasonalPriceDTO result = service.add(1L,
                booking(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31)));

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Letnja sezona");
    }

    @Test
    void add_endBeforeStart_throwsBadRequest() {
        assertThatThrownBy(() -> service.add(1L,
                booking(LocalDate.of(2026, 8, 31), LocalDate.of(2026, 7, 1))))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Kraj sezone mora biti posle pocetka");

        verify(seasonalPriceRepository, never()).save(any());
    }

    @Test
    void add_overlapsExisting_throwsBadRequest() {
        Property n = new Property();
        n.setId(1L);
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(n));
        // postojeca sezona: 1.7. - 31.8.
        when(seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(1L))
                .thenReturn(List.of(existing(2L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31))));

        // nova: 15.8. - 15.9. -> preklapa se
        assertThatThrownBy(() -> service.add(1L,
                booking(LocalDate.of(2026, 8, 15), LocalDate.of(2026, 9, 15))))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("preklapa");

        verify(seasonalPriceRepository, never()).save(any());
    }

    @Test
    void update_seasonDoesNotClashWithItself_succeeds() {
        SeasonalPrice existing = existing(2L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31));
        when(seasonalPriceRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(1L)).thenReturn(List.of(existing));
        when(seasonalPriceRepository.save(any(SeasonalPrice.class))).thenAnswer(inv -> inv.getArgument(0));

        // menjamo istu sezonu na malo drugaciji period - ne sme da prijavi preklapanje sa samom sobom
        SeasonalPriceDTO result = service.update(1L, 2L,
                booking(LocalDate.of(2026, 7, 10), LocalDate.of(2026, 8, 20)));

        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2026, 7, 10));
    }

    @Test
    void forProperty_returnsSeasons() {
        when(seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(anyLong()))
                .thenReturn(List.of(existing(2L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31))));

        List<SeasonalPriceDTO> list = service.forProperty(1L);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getPropertyId()).isEqualTo(1L);
    }
}
