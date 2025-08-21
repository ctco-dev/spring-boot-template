package lv.ctco.springboottemplate.features.greeting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greeting")
@Tag(name = "Greeting Controller", description = "Greeting endpoint")
public class GreetingController {
  private final GreetingService greetingService;

  public GreetingController(GreetingService greetingService) {
    this.greetingService = greetingService;
  }

  @GetMapping
  @Operation(summary = "Get greeting")
  public String getGreeting() {
    return greetingService.greet();
  }
}
