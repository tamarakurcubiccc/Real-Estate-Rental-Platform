package rs.realestate.rental.service;

import rs.realestate.rental.dto.AmenityDTO;

import java.util.List;

public interface AmenityService {
    List<AmenityDTO> findAll();
    AmenityDTO getById(Long id);
    AmenityDTO create(AmenityDTO dto);
    AmenityDTO update(Long id, AmenityDTO dto);
    void delete(Long id);
}
