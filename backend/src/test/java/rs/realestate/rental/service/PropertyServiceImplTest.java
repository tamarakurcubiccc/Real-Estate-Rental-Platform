package rs.realestate.rental.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.realestate.rental.dto.PropertyDTO;
import rs.realestate.rental.dto.PropertyRequestDTO;
import rs.realestate.rental.exception.ResourceNotFoundException;
import rs.realestate.rental.model.Property;
import rs.realestate.rental.model.PropertyType;
import rs.realestate.rental.model.enums.PropertyStatus;
import rs.realestate.rental.repository.PropertyRepository;
import rs.realestate.rental.repository.AmenityRepository;
import rs.realestate.rental.repository.ReviewRepository;
import rs.realestate.rental.repository.PropertyTypeRepository;
import rs.realestate.rental.service.impl.PropertyServiceImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Jedinicni (unit) testovi servisa za nekretnine.
 */
@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    @Mock private PropertyRepository propertyRepository;
    @Mock private PropertyTypeRepository typeRepository;
    @Mock private AmenityRepository amenityRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private rs.realestate.rental.repository.SeasonalPriceRepository seasonalPriceRepository;

    @InjectMocks private PropertyServiceImpl service;

    @Test
    void getById_nonExisting_throwsResourceNotFound() {
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("nije pronadjena");
    }

    @Test
    void create_setsStatusAvailable() {
        PropertyType type = new PropertyType();
        type.setId(1L);
        type.setName("Stan");

        PropertyRequestDTO dto = new PropertyRequestDTO();
        dto.setName("Test stan");
        dto.setCity("Beograd");
        dto.setRentPrice(new BigDecimal("500"));
        dto.setDeposit(new BigDecimal("500"));
        dto.setTypeId(1L);

        when(typeRepository.findById(1L)).thenReturn(Optional.of(type));
        when(propertyRepository.save(any(Property.class))).thenAnswer(inv -> {
            Property n = inv.getArgument(0);
            n.setId(10L);
            return n;
        });
        when(reviewRepository.findByPropertyIdAndApprovedTrue(anyLong()))
                .thenReturn(Collections.emptyList());

        PropertyDTO result = service.create(dto);

        assertThat(result.getStatus()).isEqualTo(PropertyStatus.AVAILABLE);
        assertThat(result.getName()).isEqualTo("Test stan");
        assertThat(result.getAverageRating()).isEqualTo(0.0);

        ArgumentCaptor<Property> captor = ArgumentCaptor.forClass(Property.class);
        verify(propertyRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(PropertyStatus.AVAILABLE);
    }

    @Test
    void create_nonExistingType_throwsResourceNotFound() {
        PropertyRequestDTO dto = new PropertyRequestDTO();
        dto.setName("Test");
        dto.setCity("Nis");
        dto.setRentPrice(new BigDecimal("300"));
        dto.setTypeId(404L);

        when(typeRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tip nije pronadjen");

        verify(propertyRepository, never()).save(any());
    }
}
