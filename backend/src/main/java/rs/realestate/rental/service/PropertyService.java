package rs.realestate.rental.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.realestate.rental.dto.PropertyDTO;
import rs.realestate.rental.dto.PropertyRequestDTO;
import rs.realestate.rental.dto.ImageDTO;
import rs.realestate.rental.model.enums.PropertyStatus;

import java.math.BigDecimal;

public interface PropertyService {

    // paginirana i sortirana pretraga sa filterima (po gradu, tipu, ceni, broju soba, statusu)
    Page<PropertyDTO> search(String city, Long typeId, BigDecimal minPrice,
                                 BigDecimal maxPrice, Integer rooms, PropertyStatus status,
                                 Pageable pageable);

    PropertyDTO getById(Long id);
    PropertyDTO create(PropertyRequestDTO dto);
    PropertyDTO update(Long id, PropertyRequestDTO dto);
    void delete(Long id);

    PropertyDTO addImage(Long propertyId, ImageDTO imageDTO);
    void deleteImage(Long propertyId, Long imageId);
}
