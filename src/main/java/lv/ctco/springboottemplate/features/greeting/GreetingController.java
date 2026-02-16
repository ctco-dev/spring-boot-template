package lv.ctco.springboottemplate.features.greeting;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/greeting")
@Tag(name = "Greeting Controller", description = "Greeting management endpoints")

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
