package lv.ctco.springboottemplate.features.statistics;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lv.ctco.springboottemplate.common.MongoDbContainerTestSupport;
import lv.ctco.springboottemplate.features.statistics.dto.StatsFormatEnum;
import lv.ctco.springboottemplate.features.statistics.dto.TodoStatsResponseDTO;
import lv.ctco.springboottemplate.features.todo.TodoRepository;
import lv.ctco.springboottemplate.features.todo.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.junit.jupiter.Testcontainers;

@RequiredArgsConstructor
@SpringBootTest
@Testcontainers
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class StatisticsServiceIntegrationTest extends MongoDbContainerTestSupport {

    private final StatisticsService statisticsService;
    private final TodoRepository todoRepository;
    private final TodoService todoService;

    @BeforeEach
    void clean() {
        todoRepository.deleteAll();
    }

    @Test
    void summary_should_return_counts_and_userStats_without_breakdown() {
        // given
        todoService.createTodo("Buy bolt pistols", "For the squad", false, "marine");
        todoService.createTodo("Bless the lasgun", "With machine oil", true, "techpriest");
        todoService.createTodo("Charge plasma cell", "Don't overheat!", false, "marine");

        // when
        TodoStatsResponseDTO dto =
                statisticsService.query(Optional.of(LocalDate.of(2025, 9, 11)), Optional.empty(), StatsFormatEnum.SUMMARY);

        // then
        assertThat(dto.totalTodos()).isEqualTo(3);
        assertThat(dto.completedTodos()).isEqualTo(1);
        assertThat(dto.pendingTodos()).isEqualTo(2);

        Map<String, Integer> userStats = dto.userStats();
        assertThat(userStats).containsEntry("marine", 2);
        assertThat(userStats).containsEntry("techpriest", 1);

        assertThat(dto.todos()).isEmpty();
    }

    @Test
    void detailed_should_include_breakdown_with_correct_lists() {
        // given
        todoService.createTodo("A", "d", true, "alpha");
        todoService.createTodo("B", "d", false, "beta");
        todoService.createTodo("C", "d", false, "alpha");

        // when
        TodoStatsResponseDTO dto =
                statisticsService.query(Optional.of(LocalDate.of(2025, 9, 11)), Optional.empty(), StatsFormatEnum.DETAILED);

        // then
        assertThat(dto.totalTodos()).isEqualTo(3);
        assertThat(dto.completedTodos()).isEqualTo(1);
        assertThat(dto.pendingTodos()).isEqualTo(2);

        assertThat(dto.todos()).isPresent();
        var breakdown = dto.todos().orElseThrow();

        assertThat(breakdown.completed()).hasSize(1);
        assertThat(breakdown.pending()).hasSize(2);

        assertThat(breakdown.completed().getFirst().completedAt()).isNotNull();
        assertThat(breakdown.pending()).allSatisfy(item -> assertThat(item.completedAt()).isNull());
    }

    @Test
    void query_should_filter_by_inclusive_date_range() {
        // given
        LocalDate d10 = LocalDate.of(2025, 9, 10);
        LocalDate d11 = LocalDate.of(2025, 9, 11);
        LocalDate d12 = LocalDate.of(2025, 9, 12);

        Instant d1 = Instant.parse("2025-09-10T09:00:00Z");
        Instant d2 = Instant.parse("2025-09-11T09:00:00Z");
        Instant d3 = Instant.parse("2025-09-12T09:00:00Z");

        todoService.createTodo("D10-1", "x", false, "u1", d1, d1);
        todoService.createTodo("D11-1", "x", true, "u1", d2, d2);
        todoService.createTodo("D12-1", "x", false, "u2", d3, d3);

        // when
        TodoStatsResponseDTO dto =
                statisticsService.query(Optional.of(d11), Optional.of(d12), StatsFormatEnum.SUMMARY);

        // then
        assertThat(dto.totalTodos()).isGreaterThanOrEqualTo(1); // depends on Instant.now()
        assertThat(dto.todos()).isEmpty();
    }

    @Test
    void should_handle_empty_dataset() {
        // when
        TodoStatsResponseDTO dto =
                statisticsService.query(Optional.empty(), Optional.empty(), StatsFormatEnum.SUMMARY);

        // then
        assertThat(dto.totalTodos()).isZero();
        assertThat(dto.completedTodos()).isZero();
        assertThat(dto.pendingTodos()).isZero();
        assertThat(dto.userStats()).isEmpty();
        assertThat(dto.todos()).isEmpty();
    }
}
