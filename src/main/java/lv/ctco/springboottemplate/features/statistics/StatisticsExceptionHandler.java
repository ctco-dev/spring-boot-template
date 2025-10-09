package lv.ctco.springboottemplate.features.statistics;

import lv.ctco.springboottemplate.features.statistics.models.StatisticsErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(assignableTypes = StatisticsController.class)
public class StatisticsExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StatisticsErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new StatisticsErrorResponse(List.of(ex.getMessage())));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StatisticsErrorResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StatisticsErrorResponse(List.of("Internal server error123")));
    }
}