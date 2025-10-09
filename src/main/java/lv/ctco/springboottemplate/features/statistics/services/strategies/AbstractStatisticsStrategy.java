package lv.ctco.springboottemplate.features.statistics.services.strategies;

import lv.ctco.springboottemplate.features.statistics.models.StatisticsQuery;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractStatisticsStrategy implements StatisticsComputationStrategy {

    protected final MongoTemplate mongoTemplate;

    protected AbstractStatisticsStrategy(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    protected MatchOperation matchDateFilter(StatisticsQuery query) {
        LocalDate from = query.from();
        LocalDate to = query.to();
        if (from == null && to == null) {
            return Aggregation.match(new Criteria());
        }
        Criteria criteria = Criteria.where("createdAt");
        if (from != null && to != null) {
            criteria.gte(from.atStartOfDay().toInstant(ZoneOffset.UTC))
                    .lte(to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusMillis(1));
        } else if (from != null) {
            criteria.gte(from.atStartOfDay().toInstant(ZoneOffset.UTC));
        } else {
            criteria.lte(to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusMillis(1));
        }

        return Aggregation.match(criteria);
    }

    protected Document aggregateSingle(Aggregation agg) {
        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "todos", Document.class);

        return results.getUniqueMappedResult();
    }

    protected Map<String, Integer> extractUserStats(Document root) {
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

    protected List<Document> getArray(Document doc, String key) {
        if (doc == null) return List.of();
        Object val = doc.get(key);
        if (val instanceof List<?> list) {
            return (list.stream()
                    .filter(Document.class::isInstance)
                    .map(Document.class::cast)
                    .toList());
        }

        return List.of();
    }

    protected Instant toInstant(Object obj) {
        if (obj instanceof Date date) {
            return date.toInstant();
        }

        if (obj instanceof Instant inst) {
            return inst;
        }

        return null;
    }
}

