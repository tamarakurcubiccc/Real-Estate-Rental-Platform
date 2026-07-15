package rs.realestate.rental.service;

import rs.realestate.rental.dto.PropertyTypeDTO;

import java.util.List;

public interface PropertyTypeService {
    List<PropertyTypeDTO> findAll();
    PropertyTypeDTO getById(Long id);
    PropertyTypeDTO create(PropertyTypeDTO dto);
    PropertyTypeDTO update(Long id, PropertyTypeDTO dto);
    void delete(Long id);
}
