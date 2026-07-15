package rs.realestate.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.realestate.rental.dto.StatisticsDTO;
import rs.realestate.rental.service.StatisticsService;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistika", description = "Zbirni statisticki podaci za administratorski pregled (samo ADMIN)")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "Statistika za admin panel",
            description = "Ukupni brojevi, raspodela po statusima, najtrazenije nekretnine i prosecne ocene.")
    @GetMapping
    public StatisticsDTO statistics() {
        return statisticsService.statistics();
    }
}
