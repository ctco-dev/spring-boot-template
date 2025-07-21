package lv.ctco.springboottemplate.features.statistics;

public record UserStatsResult(String userId, long count, long completed, long pending) {}
