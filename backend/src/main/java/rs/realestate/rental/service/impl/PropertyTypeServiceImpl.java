package rs.realestate.rental.service.impl;

import org.springframework.stereotype.Service;
import rs.realestate.rental.dto.PropertyTypeDTO;
import rs.realestate.rental.exception.BadRequestException;
import rs.realestate.rental.exception.ResourceNotFoundException;
import rs.realestate.rental.mapper.PropertyTypeMapper;
import rs.realestate.rental.model.PropertyType;
import rs.realestate.rental.repository.PropertyTypeRepository;
import rs.realestate.rental.service.PropertyTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyTypeServiceImpl implements PropertyTypeService {

    private final PropertyTypeRepository repository;

    public PropertyTypeServiceImpl(PropertyTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PropertyTypeDTO> findAll() {
        return repository.findAll().stream().map(PropertyTypeMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public PropertyTypeDTO getById(Long id) {
        return PropertyTypeMapper.toDTO(find(id));
    }

    @Override
    public PropertyTypeDTO create(PropertyTypeDTO dto) {
        if (repository.existsByName(dto.getName())) {
            throw new BadRequestException("Tip sa ovim nazivom vec postoji.");
        }
        PropertyType t = PropertyTypeMapper.toEntity(dto);
        return PropertyTypeMapper.toDTO(repository.save(t));
    }

    @Override
    public PropertyTypeDTO update(Long id, PropertyTypeDTO dto) {
        PropertyType t = find(id);
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        return PropertyTypeMapper.toDTO(repository.save(t));
    }

    @Override
    public void delete(Long id) {
        PropertyType t = find(id);
        repository.delete(t);
    }

    private PropertyType find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tip nekretnine nije pronadjen: " + id));
    }
}
