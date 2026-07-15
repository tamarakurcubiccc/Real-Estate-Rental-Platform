package rs.realestate.rental.service.impl;

import org.springframework.stereotype.Service;
import rs.realestate.rental.dto.AmenityDTO;
import rs.realestate.rental.exception.BadRequestException;
import rs.realestate.rental.exception.ResourceNotFoundException;
import rs.realestate.rental.mapper.AmenityMapper;
import rs.realestate.rental.model.Amenity;
import rs.realestate.rental.repository.AmenityRepository;
import rs.realestate.rental.service.AmenityService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository repository;

    public AmenityServiceImpl(AmenityRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AmenityDTO> findAll() {
        return repository.findAll().stream().map(AmenityMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public AmenityDTO getById(Long id) {
        return AmenityMapper.toDTO(find(id));
    }

    @Override
    public AmenityDTO create(AmenityDTO dto) {
        if (repository.existsByName(dto.getName())) {
            throw new BadRequestException("Pogodnost sa ovim nazivom vec postoji.");
        }
        Amenity p = new Amenity();
        p.setName(dto.getName());
        return AmenityMapper.toDTO(repository.save(p));
    }

    @Override
    public AmenityDTO update(Long id, AmenityDTO dto) {
        Amenity p = find(id);
        p.setName(dto.getName());
        return AmenityMapper.toDTO(repository.save(p));
    }

    @Override
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private Amenity find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pogodnost nije pronadjena: " + id));
    }
}
