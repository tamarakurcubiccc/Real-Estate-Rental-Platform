package rs.realestate.rental.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    private LocalDate date;
    private boolean approved;

    private Long userId;
    private String userFullName;

    private Long propertyId;
    private String propertyName;
}
