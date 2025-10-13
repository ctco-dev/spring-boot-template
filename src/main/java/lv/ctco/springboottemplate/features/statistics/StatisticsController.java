package lv.ctco.springboottemplate.features.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Todo Controller", description = "Todo management endpoints")
public class StatisticsController {

    public StatisticsController() {
    }

    @GetMapping
    @Operation(summary = "Get todo statistics")
    // @Todo - implement correct return dto
    public List<String> getStatistics(
            @Nullable @RequestParam(name = "from", required = false) LocalDate from,
            @Nullable @RequestParam(name = "to", required = false) LocalDate to,
            @RequestParam ResponseFormat format) {

        if (from == null && to == null) {
            throw new RuntimeException("Either 'from' or 'to' should be provided");
        }

        return List.of();
    }
}
