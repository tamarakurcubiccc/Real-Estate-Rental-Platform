package rs.realestate.rental.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.realestate.rental.dto.SeasonalPriceDTO;
import rs.realestate.rental.dto.SeasonalPriceRequestDTO;
import rs.realestate.rental.exception.BadRequestException;
import rs.realestate.rental.exception.ResourceNotFoundException;
import rs.realestate.rental.mapper.SeasonalPriceMapper;
import rs.realestate.rental.model.Property;
import rs.realestate.rental.model.SeasonalPrice;
import rs.realestate.rental.repository.PropertyRepository;
import rs.realestate.rental.repository.SeasonalPriceRepository;
import rs.realestate.rental.service.SeasonalPriceService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeasonalPriceServiceImpl implements SeasonalPriceService {

    private final SeasonalPriceRepository seasonalPriceRepository;
    private final PropertyRepository propertyRepository;

    public SeasonalPriceServiceImpl(SeasonalPriceRepository seasonalPriceRepository,
                                       PropertyRepository propertyRepository) {
        this.seasonalPriceRepository = seasonalPriceRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public List<SeasonalPriceDTO> forProperty(Long propertyId) {
        return seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(propertyId).stream()
                .map(SeasonalPriceMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SeasonalPriceDTO add(Long propertyId, SeasonalPriceRequestDTO dto) {
        validateDates(dto);
        Property n = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Nekretnina nije pronadjena: " + propertyId));

        // ne dozvoli preklapanje sa vec postojecom sezonom iste nekretnine
        validateOverlap(propertyId, dto, null);

        SeasonalPrice s = new SeasonalPrice();
        s.setProperty(n);
        s.setName(dto.getName());
        s.setStartDate(dto.getStartDate());
        s.setEndDate(dto.getEndDate());
        s.setPrice(dto.getPrice());
        return SeasonalPriceMapper.toDTO(seasonalPriceRepository.save(s));
    }

    @Override
    @Transactional
    public SeasonalPriceDTO update(Long propertyId, Long seasonalPriceId, SeasonalPriceRequestDTO dto) {
        validateDates(dto);
        SeasonalPrice s = find(propertyId, seasonalPriceId);

        // provera preklapanja sa OSTALIM sezonama (preskoci ovu koju menjamo)
        validateOverlap(propertyId, dto, seasonalPriceId);

        s.setName(dto.getName());
        s.setStartDate(dto.getStartDate());
        s.setEndDate(dto.getEndDate());
        s.setPrice(dto.getPrice());
        return SeasonalPriceMapper.toDTO(seasonalPriceRepository.save(s));
    }

    @Override
    @Transactional
    public void delete(Long propertyId, Long seasonalPriceId) {
        seasonalPriceRepository.delete(find(propertyId, seasonalPriceId));
    }

    private void validateDates(SeasonalPriceRequestDTO dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestException("Kraj sezone mora biti posle pocetka sezone.");
        }
    }

    /**
     * Dva perioda se preklapaju ako je: pocetak1 <= kraj2 i pocetak2 <= kraj1.
     * ignorisiId != null se koristi pri izmeni, da sezona ne bi "smetala sama sebi".
     */
    private void validateOverlap(Long propertyId, SeasonalPriceRequestDTO dto, Long ignoreId) {
        boolean overlap = seasonalPriceRepository.findByPropertyIdOrderByStartDateAsc(propertyId).stream()
                .filter(post -> !post.getId().equals(ignoreId))
                .anyMatch(post -> !dto.getStartDate().isAfter(post.getEndDate())
                        && !post.getStartDate().isAfter(dto.getEndDate()));
        if (overlap) {
            throw new BadRequestException("Sezona se preklapa sa vec postojecom sezonom za ovu nekretninu. "
                    + "Izaberite period koji se ne preklapa sa postojecim sezonama.");
        }
    }

    private SeasonalPrice find(Long propertyId, Long seasonalPriceId) {
        SeasonalPrice s = seasonalPriceRepository.findById(seasonalPriceId)
                .orElseThrow(() -> new ResourceNotFoundException("Stavka cenovnika nije pronadjena: " + seasonalPriceId));
        if (!s.getProperty().getId().equals(propertyId)) {
            throw new BadRequestException("Stavka cenovnika ne pripada datoj nekretnini.");
        }
        return s;
    }
}
