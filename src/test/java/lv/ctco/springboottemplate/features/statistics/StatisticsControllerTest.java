package lv.ctco.springboottemplate.features.statistics;

import lv.ctco.springboottemplate.config.ResponseFormatConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ResponseFormatConverter.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getStatisticsFailsIncorrectFormat() throws Exception {
        mockMvc.perform(get("/api/statistics")
                        .param("format", "unknown")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Method parameter 'format': Failed to convert")));
    }

    @Test
    void getStatisticsFailsMissingFromAndTo() throws Exception {
        mockMvc.perform(get("/api/statistics")
                        .param("format", "summary")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Either 'from' or 'to' should be provided")));
    }
}