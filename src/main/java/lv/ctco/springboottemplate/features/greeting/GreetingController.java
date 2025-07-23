package lv.ctco.springboottemplate.features.greeting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/greeting")
@Tag(name = "Greeting Controller", description = "Greeting endpoints")
public class GreetingController {

  private final GreetingService greetingService;

  @GetMapping
  @Operation(summary = "Greet (counts open todos)")
  public String greet() {
    return greetingService.greet();
  }
}
