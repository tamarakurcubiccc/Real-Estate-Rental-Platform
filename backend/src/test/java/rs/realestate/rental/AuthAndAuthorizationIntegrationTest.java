package rs.realestate.rental;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import rs.realestate.rental.model.User;
import rs.realestate.rental.model.enums.Role;
import rs.realestate.rental.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integracioni testovi: pokrecu ceo Spring kontekst (@SpringBootTest) uz H2 bazu
 * i testiraju stvarni HTTP tok - registraciju, prijavu i role-based autorizaciju.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndAuthorizationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void seedAdmin() {
        userRepository.deleteAll();
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("Adminic");
        admin.setEmail("admin@test.rs");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    private String login(String email, String password) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        return node.get("token").asText();
    }

    @Test
    void register_createsUserAndReturnsToken() throws Exception {
        String body = "{\"firstName\":\"Nik\",\"lastName\":\"Nikic\",\"email\":\"nik@test.rs\","
                + "\"password\":\"lozinka123\",\"phone\":\"060\"}";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("nik@test.rs"))
                .andExpect(jsonPath("$.user.role").value("USER"));
    }

    @Test
    void publicGet_properties_accessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk());
    }

    @Test
    void createProperty_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"city\":\"BG\",\"rentPrice\":100,\"typeId\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProperty_withUserToken_returns403() throws Exception {
        // registruj obicnog korisnika i uzmi njegov token
        String reg = "{\"firstName\":\"Ob\",\"lastName\":\"Ob\",\"email\":\"ob@test.rs\","
                + "\"password\":\"lozinka123\"}";
        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(reg))
                .andReturn().getResponse().getContentAsString();
        String userToken = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(post("/api/properties")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"city\":\"BG\",\"rentPrice\":100,\"typeId\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminFlow_createsTypeThenProperty_returns201() throws Exception {
        String adminToken = login("admin@test.rs", "admin123");

        // 1) admin kreira tip nekretnine
        String typeResponse = mockMvc.perform(post("/api/property-types")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Stan\",\"description\":\"Test\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long typeId = objectMapper.readTree(typeResponse).get("id").asLong();

        // 2) admin kreira nekretninu koristeci taj tip
        String property = "{\"name\":\"Lep stan\",\"city\":\"Beograd\","
                + "\"rentPrice\":450,\"deposit\":450,\"typeId\":" + typeId + "}";
        mockMvc.perform(post("/api/properties")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(property))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Lep stan"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }
}
