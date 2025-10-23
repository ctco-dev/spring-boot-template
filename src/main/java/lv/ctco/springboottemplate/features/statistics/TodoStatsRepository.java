package lv.ctco.springboottemplate.features.statistics;

import lv.ctco.springboottemplate.features.todo.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
@Repository
public class TodoStatsRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public TodoSummaryStatsDto getStats() {
        long total = mongoTemplate.count(new Query(), Todo.class);
        long completed = mongoTemplate.count(Query.query(Criteria.where("completed").is(true)), Todo.class);
        long pending = total - completed;

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("createdBy").count().as("count")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "todos", Document.class);
        Map<String, Long> userStats = new HashMap<>();
        for (Document doc : results.getMappedResults()) {
            userStats.put(doc.getString("_id"), doc.getInteger("count").longValue());
        }

        return new TodoSummaryStatsDto(total, completed, pending, userStats);
    }
}
