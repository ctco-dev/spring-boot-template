package lv.ctco.springboottemplate.features.statistics.services;

import lv.ctco.springboottemplate.features.statistics.models.StatisticsQuery;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Repository
public class StatisticsRepository {

    private final MongoTemplate mongoTemplate;

    public StatisticsRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Document executeSummary(StatisticsQuery query) {
        Aggregation agg = Aggregation.newAggregation(
                matchDateFilter(query),
                Aggregation.facet(
                                Aggregation.group()
                                        .count().as("total")
                                        .sum(ConditionalOperators.when(Criteria.where("completed").is(true)).then(1).otherwise(0)).as("completed")
                        ).as("counts")
                        .and(Aggregation.group("createdBy").count().as("count")).as("userStats")
        );
        return aggregate(agg);
    }

    public Document executeDetailed(StatisticsQuery query) {
        ProjectionOperation projectTodosFields = Aggregation.project("title", "createdBy", "createdAt", "updatedAt", "completed", "completedAt");
        Aggregation agg = Aggregation.newAggregation(
                matchDateFilter(query),
                Aggregation.facet(
                                Aggregation.group()
                                        .count().as("total")
                                        .sum(ConditionalOperators.when(Criteria.where("completed").is(true)).then(1).otherwise(0)).as("completed")
                        ).as("counts")
                        .and(Aggregation.group("createdBy").count().as("count")).as("userStats")
                        .and(Aggregation.match(Criteria.where("completed").is(true)), projectTodosFields).as("completedTodosSource")
                        .and(Aggregation.match(Criteria.where("completed").is(false)), projectTodosFields).as("pendingTodosSource")
        );
        return aggregate(agg);
    }

    private Document aggregate(Aggregation aggregation) {
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "todos", Document.class);
        return results.getUniqueMappedResult();
    }

    private MatchOperation matchDateFilter(StatisticsQuery query) {
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
}

