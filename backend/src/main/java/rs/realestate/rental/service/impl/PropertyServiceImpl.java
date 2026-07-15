package rs.realestate.rental.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.realestate.rental.dto.PropertyDTO;
import rs.realestate.rental.dto.PropertyRequestDTO;
import rs.realestate.rental.dto.ImageDTO;
import rs.realestate.rental.exception.ResourceNotFoundException;
import rs.realestate.rental.mapper.PropertyMapper;
import rs.realestate.rental.model.Property;
import rs.realestate.rental.model.Amenity;
import rs.realestate.rental.model.Image;
import rs.realestate.rental.model.PropertyType;
import rs.realestate.rental.model.Review;
import rs.realestate.rental.model.enums.PropertyStatus;
import rs.realestate.rental.repository.PropertyRepository;
import rs.realestate.rental.repository.AmenityRepository;
import rs.realestate.rental.repository.ReviewRepository;
import rs.realestate.rental.repository.SeasonalPriceRepository;
import rs.realestate.rental.repository.PropertyTypeRepository;
import rs.realestate.rental.service.PropertyService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyTypeRepository typeRepository;
    private final AmenityRepository amenityRepository;
    private final ReviewRepository reviewRepository;
    private final SeasonalPriceRepository seasonalPriceRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository,
                                 PropertyTypeRepository typeRepository,
                                 AmenityRepository amenityRepository,
                                 ReviewRepository reviewRepository,
                                 SeasonalPriceRepository seasonalPriceRepository) {
        this.propertyRepository = propertyRepository;
        this.typeRepository = typeRepository;
        this.amenityRepository = amenityRepository;
        this.reviewRepository = reviewRepository;
        this.seasonalPriceRepository = seasonalPriceRepository;
    }

    @Override
    public Page<PropertyDTO> search(String city, Long typeId, BigDecimal minPrice,
                                        BigDecimal maxPrice, Integer rooms, PropertyStatus status,
                                        Pageable pageable) {
        return propertyRepository.findAll(spec(city, typeId, minPrice, maxPrice, rooms, status), pageable)
                .map(this::toDTO);
    }

    // dinamicko filtriranje: u upit ulaze samo prosledjeni (ne-null) kriterijumi
    private Specification<Property> spec(String city, Long typeId, BigDecimal minPrice,
                                           BigDecimal maxPrice, Integer rooms, PropertyStatus status) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (city != null && !city.isBlank()) {
                p.add(cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
            }
            if (typeId != null) {
                p.add(cb.equal(root.get("type").get("id"), typeId));
            }
            if (minPrice != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("rentPrice"), minPrice));
            }
            if (maxPrice != null) {
                p.add(cb.lessThanOrEqualTo(root.get("rentPrice"), maxPrice));
            }
            if (rooms != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("rooms"), rooms));
            }
            if (status != null) {
                p.add(cb.equal(root.get("status"), status));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    @Override
    public PropertyDTO getById(Long id) {
        return toDTO(find(id));
    }

    @Override
    @Transactional
    public PropertyDTO create(PropertyRequestDTO dto) {
        Property n = new Property();
        populate(n, dto);
        n.setStatus(PropertyStatus.AVAILABLE);
        return toDTO(propertyRepository.save(n));
    }

    @Override
    @Transactional
    public PropertyDTO update(Long id, PropertyRequestDTO dto) {
        Property n = find(id);
        populate(n, dto);
        return toDTO(propertyRepository.save(n));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Property n = find(id);
        // sezone se brisu prve, inace bi strani kljuc sprecio brisanje nekretnine
        seasonalPriceRepository.deleteByPropertyId(id);
        propertyRepository.delete(n);
    }

    @Override
    @Transactional
    public PropertyDTO addImage(Long propertyId, ImageDTO imageDTO) {
        Property n = find(propertyId);
        Image s = new Image();
        s.setUrl(imageDTO.getUrl());
        s.setPrimary(imageDTO.isPrimary());
        s.setProperty(n);
        n.getImages().add(s);
        return toDTO(propertyRepository.save(n));
    }

    @Override
    @Transactional
    public void deleteImage(Long propertyId, Long imageId) {
        Property n = find(propertyId);
        boolean removed = n.getImages().removeIf(s -> s.getId().equals(imageId));
        if (!removed) {
            throw new ResourceNotFoundException("Slika nije pronadjena: " + imageId);
        }
        propertyRepository.save(n);
    }

    // ---- pomocne metode ----

    private void populate(Property n, PropertyRequestDTO dto) {
        n.setName(dto.getName());
        n.setAddress(dto.getAddress());
        n.setCity(dto.getCity());
        n.setArea(dto.getArea());
        n.setRooms(dto.getRooms());
        n.setFloor(dto.getFloor());
        n.setRentPrice(dto.getRentPrice());
        n.setDeposit(dto.getDeposit());
        n.setDescription(dto.getDescription());

        PropertyType type = typeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tip nije pronadjen: " + dto.getTypeId()));
        n.setType(type);

        Set<Amenity> amenities = new HashSet<>();
        if (dto.getAmenityIds() != null) {
            for (Long pid : dto.getAmenityIds()) {
                Amenity p = amenityRepository.findById(pid)
                        .orElseThrow(() -> new ResourceNotFoundException("Pogodnost nije pronadjena: " + pid));
                amenities.add(p);
            }
        }
        n.setAmenities(amenities);
    }

    private PropertyDTO toDTO(Property n) {
        List<Review> approved = reviewRepository.findByPropertyIdAndApprovedTrue(n.getId());
        double average = approved.isEmpty() ? 0.0 :
                approved.stream().mapToInt(Review::getRating).average().orElse(0.0);
        return PropertyMapper.toDTO(n, Math.round(average * 10.0) / 10.0, approved.size());
    }

    private Property find(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nekretnina nije pronadjena: " + id));
    }
}
