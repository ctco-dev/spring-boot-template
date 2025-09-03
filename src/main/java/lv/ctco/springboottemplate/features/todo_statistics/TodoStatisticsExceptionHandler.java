package lv.ctco.springboottemplate.features.todo_statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TodoStatisticsExceptionHandler {
  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(Exception ex) {
    List<String> errors = new ArrayList<>();

    if (ex instanceof MethodArgumentNotValidException invalidArgumentException) {
      errors =
          invalidArgumentException.getBindingResult().getAllErrors().stream()
              .map(DefaultMessageSourceResolvable::getDefaultMessage)
              .toList();
    } else if (ex instanceof BindException be) {
      errors =
          be.getBindingResult().getAllErrors().stream()
              .map(DefaultMessageSourceResolvable::getDefaultMessage)
              .toList();
    }

    return ResponseEntity.badRequest()
        .body(Map.of("status", 400, "error", "Bad Request", "messages", errors));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, String>> handleServerError(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Internal server error", "details", ex.getMessage()));
  }
}
