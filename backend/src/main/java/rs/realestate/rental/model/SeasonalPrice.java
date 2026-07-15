package rs.realestate.rental.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Sezonska cena zakupa za nekretninu u odredjenom periodu
 * (npr. vise cene za vile na planinama tokom zimske sezone).
 * Ako dan zakupa pada u definisanu sezonu, koristi se ova cena
 * umesto podrazumevane mesecne cene nekretnine.
 */
@Entity
@Table(name = "seasonal_price")
@Getter
@Setter
@NoArgsConstructor
public class SeasonalPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // npr. "Zimska sezona"

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal price; // mesecna cena u toj sezoni

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
}
