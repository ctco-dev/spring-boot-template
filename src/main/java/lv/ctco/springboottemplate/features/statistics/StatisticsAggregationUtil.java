package lv.ctco.springboottemplate.features.statistics;

import org.bson.Document;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StatisticsAggregationUtil {
    public static Map<String, Integer> extractUserStats(Document root) {
        if (root == null) return Map.of();
        List<Document> userStatsDocs = getArray(root, "userStats");
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Document d : userStatsDocs) {
            String user = d.getString("_id");
            int count = d.getInteger("count", 0);
            map.put(user, count);
        }
        return map;
    }

    public static List<Document> getArray(Document doc, String key) {
        if (doc == null) return List.of();
        Object val = doc.get(key);
        if (val instanceof List<?> list) {
            return list.stream().filter(Document.class::isInstance).map(Document.class::cast).toList();
        }
        return List.of();
    }
}