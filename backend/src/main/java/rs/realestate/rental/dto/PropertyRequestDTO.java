package rs.realestate.rental.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class PropertyRequestDTO {

    @NotBlank(message = "Naziv je obavezan.")
    private String name;

    private String address;

    @NotBlank(message = "Grad je obavezan.")
    private String city;

    @PositiveOrZero(message = "Kvadratura ne moze biti negativna.")
    private double area;

    @PositiveOrZero(message = "Broj soba ne moze biti negativan.")
    private int rooms;

    private int floor;

    @NotNull(message = "Cena zakupa je obavezna.")
    @Positive(message = "Cena zakupa mora biti veca od nule.")
    private BigDecimal rentPrice;

    @PositiveOrZero(message = "Depozit ne moze biti negativan.")
    private BigDecimal deposit;

    private String description;

    @NotNull(message = "Tip nekretnine je obavezan.")
    private Long typeId;

    private Set<Long> amenityIds;
}
