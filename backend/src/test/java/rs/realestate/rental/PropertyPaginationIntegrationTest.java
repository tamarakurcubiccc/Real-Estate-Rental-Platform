package rs.realestate.rental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import rs.realestate.rental.model.Property;
import rs.realestate.rental.model.PropertyType;
import rs.realestate.rental.repository.PropertyRepository;
import rs.realestate.rental.repository.PropertyTypeRepository;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integracioni testovi paginacije javne ponude nekretnina.
 * Podrazumevano se vraca 3 nekretnine po strani, sortirano od najnovije dodate.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PropertyPaginationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private PropertyRepository propertyRepository;
    @Autowired private PropertyTypeRepository typeRepository;

    @BeforeEach
    void seed() {
        propertyRepository.deleteAll();
        typeRepository.deleteAll();

        PropertyType apartment = new PropertyType();
        apartment.setName("Stan");
        apartment.setDescription("Test tip");
        PropertyType savedType = typeRepository.save(apartment);

        // 5 nekretnina -> 2 strane (3 + 2)
        for (int i = 1; i <= 5; i++) {
            Property n = new Property();
            n.setName("Nekretnina " + i);
            n.setCity(i <= 3 ? "Beograd" : "Novi Sad");
            n.setArea(50 + i);
            n.setRooms(2);
            n.setFloor(1);
            n.setRentPrice(new BigDecimal("500"));
            n.setDeposit(new BigDecimal("500"));
            n.setType(savedType);
            propertyRepository.save(n);
        }
    }

    @Test
    void firstPage_returnsThreeProperties() throws Exception {
        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    void secondPage_returnsRemainingTwo() throws Exception {
        mockMvc.perform(get("/api/properties").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void sorting_newestFirstOnFirstPage() throws Exception {
        // sort je opadajuce po id-u -> poslednja ubacena je prva
        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Nekretnina 5"));
    }

    @Test
    void filterIsAppliedWithPagination() throws Exception {
        // samo 3 nekretnine su u Beogradu -> jedna strana
        mockMvc.perform(get("/api/properties").param("city", "Beograd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}
