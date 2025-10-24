package lv.ctco.springboottemplate.features.statistics;

import java.util.List;

public record TodoDetails(
        List<CompletedTodo> completed,
        List<PendingTodo> pending
) {}
