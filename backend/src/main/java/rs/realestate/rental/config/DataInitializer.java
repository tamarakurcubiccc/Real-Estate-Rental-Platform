package rs.realestate.rental.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.realestate.rental.model.*;
import rs.realestate.rental.model.enums.PropertyStatus;
import rs.realestate.rental.model.enums.Role;
import rs.realestate.rental.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyTypeRepository typeRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyRepository propertyRepository;
    private final ReviewRepository reviewRepository;
    private final SeasonalPriceRepository seasonalPriceRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PropertyTypeRepository typeRepository,
                           AmenityRepository amenityRepository, PropertyRepository propertyRepository,
                           ReviewRepository reviewRepository, SeasonalPriceRepository seasonalPriceRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.typeRepository = typeRepository;
        this.amenityRepository = amenityRepository;
        this.propertyRepository = propertyRepository;
        this.reviewRepository = reviewRepository;
        this.seasonalPriceRepository = seasonalPriceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return; // vec inicijalizovano

        // korisnici
        User admin = new User();
        admin.setFirstName("Admin"); admin.setLastName("Administrator");
        admin.setEmail("admin@nekretnine.rs"); admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setPhone("0600000000"); admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        User pera = new User();
        pera.setFirstName("Pera"); pera.setLastName("Peric");
        pera.setEmail("pera@example.com"); pera.setPassword(passwordEncoder.encode("pera123"));
        pera.setPhone("0611111111"); pera.setRole(Role.USER);
        userRepository.save(pera);

        // tipovi nekretnina (sifarnik)
        PropertyType apartment = saveType("Stan", "Stambena jedinica u zgradi");
        PropertyType house = saveType("Kuca", "Samostalni stambeni objekat");
        PropertyType villa = saveType("Vila", "Luksuzni objekat, cesto na planini ili moru");
        PropertyType parkingType = saveType("Parking mesto", "Garazno ili otvoreno parking mesto");
        saveType("Poslovni prostor", "Kancelarijski ili lokal prostor");

        // pogodnosti (sifarnik, M:N)
        Amenity wifi = saveAmenity("Wi-Fi");
        Amenity parkingAmenity = saveAmenity("Parking");
        Amenity elevator = saveAmenity("Lift");
        Amenity airConditioning = saveAmenity("Klima");
        Amenity terrace = saveAmenity("Terasa");
        Amenity garage = saveAmenity("Garaza");

        // ---------------- nekretnine ----------------

        // n1 - jednoiposoban stan, Vracar, kod Hrama Svetog Save
        Property n1 = new Property();
        n1.setName("Jednoiposoban stan na Vracaru kod Hrama Svetog Save");
        n1.setAddress("Brane Crncevica"); n1.setCity("Beograd");
        n1.setArea(37); n1.setRooms(2); n1.setFloor(2);
        n1.setRentPrice(new BigDecimal("550")); n1.setDeposit(new BigDecimal("550"));
        n1.setDescription("Jednoiposoban stan od 37 m2 na drugom spratu petospratnice, izgradjen 2006. " +
                "Kompletno opremljen i spreman za useljenje, sa terasom okrenutom ka dvoristu, " +
                "alu-drvo stolarijom, klimom, centralnim grejanjem i visinom plafona 2,85 m. " +
                "U cenu je ukljuceno i parking mesto ispod rampe zgrade.");
        n1.setStatus(PropertyStatus.AVAILABLE); n1.setType(apartment);
        n1.setAmenities(setOf(wifi, elevator, airConditioning, parkingAmenity, terrace));
        addImage(n1, "https://cdn.divisnekretnine.rs/nekretnina/25308/prodaja-jednoiposoban-stan-vracar-hram-brane-crncevica-37m2-dnevni-boravak%20(1)-fullscreen.jpeg", true);
        addImage(n1, "https://cdn.divisnekretnine.rs/nekretnina/25308/prodaja-jednoiposoban-stan-vracar-hram-brane-crncevica-37m2-kuhinja%20(2)-fullscreen.jpeg", false);
        addImage(n1, "https://cdn.divisnekretnine.rs/nekretnina/25308/prodaja-jednoiposoban-stan-vracar-hram-brane-crncevica-37m2-spavaca-soba%20(3)-fullscreen.jpeg", false);
        addImage(n1, "https://cdn.divisnekretnine.rs/nekretnina/25308/prodaja-jednoiposoban-stan-vracar-hram-brane-crncevica-37m2-kupatilo%20(4)-fullscreen.jpeg", false);
        propertyRepository.save(n1);

        // n2 - Gorski Hotel & Spa, centar Kopaonika (ski-in/ski-out)
        Property n2 = new Property();
        n2.setName("Gorski Hotel & Spa - Kopaonik");
        n2.setAddress("Centar Kopaonika (ski-in/ski-out)"); n2.setCity("Kopaonik");
        n2.setArea(100); n2.setRooms(3); n2.setFloor(4);
        n2.setRentPrice(new BigDecimal("700")); n2.setDeposit(new BigDecimal("350"));
        n2.setDescription("Luksuzni apartman u hotelu Gorski 4* na ekskluzivnoj lokaciji u centru Kopaonika, " +
                "svega 130 m od ski centra - staze su direktno dostupne iz objekta (ski-in/ski-out). " +
                "Gostima je na raspolaganju odlicno opremljen Spa centar na 1.100 m2, sa unutrasnjim i " +
                "spoljnim grejanim bazenima s pogledom na staze, finskom i borovom saunom, turskim kupatilom, " +
                "ledenom fontanom, so-sobom i fitnes centrom. U sklopu hotela su restoran, lobi bar, garaza i " +
                "ski ostava. Apartman ima podno grejanje, LCD TV, minibar, sef i besplatan Wi-Fi.");
        n2.setStatus(PropertyStatus.AVAILABLE); n2.setType(villa);
        n2.setAmenities(setOf(wifi, parkingAmenity, garage, elevator, airConditioning));
        addImage(n2, "https://barcino.travel/wp-content/uploads/2020/11/Srbija_Kopaonik_Hotel_Gorski_Barcino-1.jpg", true);
        addImage(n2, "https://barcino.travel/wp-content/uploads/2020/11/Srbija_Kopaonik_Hotel_Gorski_Barcino-2.jpg", false);
        addImage(n2, "https://barcino.travel/wp-content/uploads/2020/11/Srbija_Kopaonik_Hotel_Gorski_Barcino-3.jpg", false);
        addImage(n2, "https://barcino.travel/wp-content/uploads/2020/11/Srbija_Kopaonik_Hotel_Gorski_Barcino-4.jpg", false);
        addImage(n2, "https://barcino.travel/wp-content/uploads/2020/11/Srbija_Kopaonik_Hotel_Gorski_Barcino-5.jpg", false);
        addImage(n2, "https://barcino.travel/wp-content/uploads/2020/11/Srbija_Kopaonik_Hotel_Gorski_Barcino-6.jpg", false);
        addImage(n2, "https://barcino.travel/wp-content/uploads/2020/11/Srbija_Kopaonik_Hotel_Gorski_Barcino-7.jpg", false);
        addImage(n2, "https://barcino.travel/wp-content/uploads/2020/11/Srbija_Kopaonik_Hotel_Gorski_Barcino-8.jpg", false);
        addImage(n2, "https://barcino.travel/wp-content/uploads/2020/11/Srbija_Kopaonik_Hotel_Gorski_Barcino-9.jpg", false);
        addImage(n2, "https://barcino.travel/wp-content/uploads/2020/11/Srbija_Kopaonik_Hotel_Gorski_Barcino-10.jpg", false);
        propertyRepository.save(n2);

        // n3 - garazno mesto, Beograd na vodi (Metropolitan)
        Property n3 = new Property();
        n3.setName("Garazno mesto - Beograd na vodi (Metropolitan)");
        n3.setAddress("Bulevar Vudroa Vilsona"); n3.setCity("Beograd");
        n3.setArea(13); n3.setRooms(0); n3.setFloor(-1);
        n3.setRentPrice(new BigDecimal("100")); n3.setDeposit(new BigDecimal("100"));
        n3.setDescription("Odlicno garazno mesto na cvrstoj podlozi na nivou -1 u objektu Metropolitan u " +
                "Beogradu na vodi. Objekat ima lift, recepciju i obezbedjenje 24/7. U neposrednoj " +
                "blizini Kula Beograd (BW Tower) i trzni centar Galerija.");
        n3.setStatus(PropertyStatus.AVAILABLE); n3.setType(parkingType);
        n3.setAmenities(setOf(parkingAmenity, elevator));
        addImage(n3, "https://resizer2.4zida.rs/GZ11oXUj9GSr3jlz7B4dLDFnsZOsOG0kr8WfttEsNKU/rs:fit:1920:1080:0/bG9jYWw6Ly8vNjc4YTNkYTY3NzVhYzExMDUzMGQ0MjZiLzAwOThiNmZmMjlfd20.webp", true);
        addImage(n3, "https://resizer2.4zida.rs/ZC_x48ejhh7pg9n4EYwS3gj0Tc2N0G2kpHunmud6jrY/rs:fit:1920:1080:0/bG9jYWw6Ly8vNjc4YTNkYTY3NzVhYzExMDUzMGQ0MjZiL2UyN2ZjODU2Mjdfd20.webp", false);
        propertyRepository.save(n3);

        // n4 - kuca sa bazenom, Miljakovac 3 (Rakovica)
        Property n4 = new Property();
        n4.setName("Kuca sa bazenom - Miljakovac 3");
        n4.setAddress("Miljakovacke livade"); n4.setCity("Beograd");
        n4.setArea(459); n4.setRooms(6); n4.setFloor(1);
        n4.setRentPrice(new BigDecimal("1500")); n4.setDeposit(new BigDecimal("1500"));
        n4.setDescription("Prostrana kuca u naselju Miljakovac 3 (Rakovica), sa dve zasebne stambene " +
                "jedinice i dve garaze na placu od 12 ari. Poseduje bazen dimenzija 12x6 m, " +
                "letnjikovac i etazno grejanje. Uknjizena, na sve popularnijoj lokaciji.");
        n4.setStatus(PropertyStatus.AVAILABLE); n4.setType(house);
        n4.setAmenities(setOf(wifi, parkingAmenity, terrace, garage));
        addImage(n4, "https://www.berzanekretnina.org/webp/naslovna-slika/81/1753175-900.webp", true);
        for (int i = 1753176; i <= 1753182; i++) {
            addImage(n4, "https://berzanekretnina.org/slike-nekretnina/v/" + i + ".jpg", false);
        }
        propertyRepository.save(n4);

        // n5 - vila "Zaovinska idila", Tara / selo Zaovine
        Property n5 = new Property();
        n5.setName("Vila Zaovinska idila - Tara");
        n5.setAddress("Zaovine (Zaovinsko jezero)"); n5.setCity("Tara");
        n5.setArea(180); n5.setRooms(6); n5.setFloor(2);
        n5.setRentPrice(new BigDecimal("1000")); n5.setDeposit(new BigDecimal("500"));
        n5.setDescription("Tara, selo Zaovine (14 km od Mitrovca). Savrsen odmor u prirodi okruzen sumom i " +
                "cistim vazduhom, sa pogledom na Zaovinsko jezero i bazenom u seoskom stilu. " +
                "Idealno za miran odmor daleko od gradske vreve.");
        n5.setStatus(PropertyStatus.AVAILABLE); n5.setType(villa);
        n5.setAmenities(setOf(wifi, parkingAmenity, terrace));
        addImage(n5, "https://selo.rs/storage/upload/images/offers/1180/1180_3Aqy3IKSNO_xl_th_off.jpg", true);
        addImage(n5, "https://selo.rs/storage/upload/images/offers/1180/1180_7UroWYbJdm_xl_th_off.jpg", false);
        addImage(n5, "https://selo.rs/storage/upload/images/offers/1180/1180_DosUPQt4Yt_xl_th_off.jpg", false);
        addImage(n5, "https://selo.rs/storage/upload/images/offers/1180/1180_KI93b2l64m_xl_th_off.jpg", false);
        addImage(n5, "https://selo.rs/storage/upload/images/offers/1180/1180_KU2OS2eecd_xl_th_off.jpg", false);
        addImage(n5, "https://selo.rs/storage/upload/images/offers/1180/1180_KwzBfB4uA1_xl_th_off.jpg", false);
        addImage(n5, "https://selo.rs/storage/upload/images/offers/1180/1180_LPHPs0nPS7_xl_th_off.jpg", false);
        addImage(n5, "https://selo.rs/storage/upload/images/offers/1180/1180_MBpFauugqD_xl_th_off.jpg", false);
        propertyRepository.save(n5);

        // n6 - vila Loukakis, Sarti / Sitonija (Grcka)
        Property n6 = new Property();
        n6.setName("Vila Loukakis - Sarti, Grcka");
        n6.setAddress("Sarti, Sitonija (Halkidiki)"); n6.setCity("Sarti");
        n6.setArea(120); n6.setRooms(4); n6.setFloor(2);
        n6.setRentPrice(new BigDecimal("700")); n6.setDeposit(new BigDecimal("350"));
        n6.setDescription("Vila u mestu Sarti na poluostrvu Sitonija (Halkidiki, Grcka), nadomak plaze. " +
                "Klimatizovani apartmani sa opremljenom kuhinjom, terasom i parkingom - idealno za " +
                "letovanje uz cisto more i borovu sumu.");
        n6.setStatus(PropertyStatus.AVAILABLE); n6.setType(villa);
        n6.setAmenities(setOf(wifi, parkingAmenity, airConditioning, terrace));
        addImage(n6, "https://www.sabra.rs/uploaded_pictures/content/putovanja/1000x634/vila-loukakis-sarti-7870.jpg", true);
        addImage(n6, "https://www.sabra.rs/uploaded_pictures/content/putovanja/1000x634/vila-loukakis-sarti-7870-1.jpg", false);
        addImage(n6, "https://www.sabra.rs/uploaded_pictures/content/putovanja/1000x634/vila-loukakis-sarti-7870-2.jpg", false);
        addImage(n6, "https://www.sabra.rs/uploaded_pictures/content/putovanja/1000x634/vila-loukakis-sarti-7870-3.jpg", false);
        addImage(n6, "https://www.sabra.rs/uploaded_pictures/content/putovanja/1000x634/vila-loukakis-sarti-7870-4.jpg", false);
        addImage(n6, "https://www.sabra.rs/uploaded_pictures/content/putovanja/1000x634/vila-loukakis-sarti-7870-5.jpg", false);
        addImage(n6, "https://www.sabra.rs/uploaded_pictures/content/putovanja/1000x634/vila-loukakis-sarti-7870-6.jpg", false);
        propertyRepository.save(n6);

        // ---------------- odobrene recenzije ----------------
        Review r1 = new Review();
        r1.setUser(pera); r1.setProperty(n1); r1.setRating(5);
        r1.setComment("Odlican stan, sve kao na slikama!"); r1.setApproved(true);
        reviewRepository.save(r1);

        Review r2 = new Review();
        r2.setUser(pera); r2.setProperty(n2); r2.setRating(4);
        r2.setComment("Fenomenalan spa i bazeni sa pogledom na staze, ski-in/ski-out do vrata. Vredi svake pare!"); r2.setApproved(true);
        reviewRepository.save(r2);

        Review r3 = new Review();
        r3.setUser(pera); r3.setProperty(n5); r3.setRating(5);
        r3.setComment("Predivno mesto za odmor, mir i priroda. Bazen i pogled na jezero su fenomenalni!");
        r3.setApproved(true);
        reviewRepository.save(r3);

        Review r4 = new Review();
        r4.setUser(pera); r4.setProperty(n6); r4.setRating(5);
        r4.setComment("Vila tik uz plazu, cisto more i mirno okruzenje. Apartman klimatizovan i uredan!");
        r4.setApproved(true);
        reviewRepository.save(r4);

        // ---------------- sezonski cenovnik ----------------
        // Gorski Hotel & Spa na Kopaoniku (n2): zimska sezona skuplja, letnja jeftinija
        int year = LocalDate.now().getYear();
        addSeason(n2, "Zimska sezona", LocalDate.of(year, 12, 1), LocalDate.of(year + 1, 3, 1),
                new BigDecimal("900"));
        addSeason(n2, "Letnja sezona", LocalDate.of(year, 6, 15), LocalDate.of(year, 9, 1),
                new BigDecimal("450"));

        // vila na Tari (n5)
        addSeason(n5, "Letnja sezona", LocalDate.of(year, 6, 15), LocalDate.of(year, 9, 1),
                new BigDecimal("1200"));
        addSeason(n5, "Novogodisnji praznici", LocalDate.of(year, 12, 25), LocalDate.of(year + 1, 1, 10),
                new BigDecimal("1300"));

        // vila u Sartiju (n6)
        addSeason(n6, "Predsezona", LocalDate.of(year, 6, 1), LocalDate.of(year, 6, 30),
                new BigDecimal("800"));
        addSeason(n6, "Glavna sezona (jul-avgust)", LocalDate.of(year, 7, 1), LocalDate.of(year, 8, 31),
                new BigDecimal("1100"));
        addSeason(n6, "Postsezona", LocalDate.of(year, 9, 1), LocalDate.of(year, 9, 30),
                new BigDecimal("800"));

        System.out.println(">>> Pocetni podaci ubaceni. Admin: admin@nekretnine.rs / admin123");
    }

    private void addSeason(Property n, String name, LocalDate od, LocalDate endDate, BigDecimal price) {
        SeasonalPrice s = new SeasonalPrice();
        s.setProperty(n);
        s.setName(name);
        s.setStartDate(od);
        s.setEndDate(endDate);
        s.setPrice(price);
        seasonalPriceRepository.save(s);
    }

    private PropertyType saveType(String name, String description) {
        PropertyType t = new PropertyType();
        t.setName(name); t.setDescription(description);
        return typeRepository.save(t);
    }

    private Amenity saveAmenity(String name) {
        Amenity p = new Amenity();
        p.setName(name);
        return amenityRepository.save(p);
    }

    private Set<Amenity> setOf(Amenity... p) {
        Set<Amenity> s = new HashSet<>();
        for (Amenity x : p) s.add(x);
        return s;
    }

    private void addImage(Property n, String url, boolean primary) {
        Image s = new Image();
        s.setUrl(url); s.setPrimary(primary); s.setProperty(n);
        n.getImages().add(s);
    }
}
