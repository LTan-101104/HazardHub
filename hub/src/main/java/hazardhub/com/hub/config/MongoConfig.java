package hazardhub.com.hub.config;

import hazardhub.com.hub.model.entity.Route;
import hazardhub.com.hub.model.entity.Hazard;
import hazardhub.com.hub.model.entity.SOSEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.Optional;

@Configuration
@EnableMongoAuditing
@RequiredArgsConstructor
@Slf4j
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    @Bean
    public AuditorAware<String> auditorAware() {
        // TODO: Replace with actual user from Firebase Auth context
        return () -> Optional.of("admin");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initHazardIndexesAfterStartup() {
        IndexOperations indexOps = mongoTemplate.indexOps(Hazard.class);

        boolean locationIndexExists = indexOps.getIndexInfo().stream()
                .anyMatch(indexInfo -> indexInfo.getIndexFields().stream()
                        .anyMatch(field -> "location".equals(field.getKey())));

        if (!locationIndexExists) {
            indexOps.createIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE));
            log.info("Created 2dsphere index on 'location' field for Hazard collection");
        } else {
            log.info("Index on 'location' field already exists for Hazard collection");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initSOSEventIndexesAfterStartup() {
        IndexOperations indexOps = mongoTemplate.indexOps(SOSEvent.class);

        boolean locationIndexExists = indexOps.getIndexInfo().stream()
                .anyMatch(indexInfo -> indexInfo.getIndexFields().stream()
                        .anyMatch(field -> "location".equals(field.getKey())));

        if (!locationIndexExists) {
            indexOps.createIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE));
            log.info("Created 2dsphere index on 'location' field for SOSEvent collection");
        } else {
            log.info("Index on 'location' field already exists for SOSEvent collection");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initRouteIndexesAfterStartup() {
        IndexOperations indexOps = mongoTemplate.indexOps(Route.class);

        boolean tripSelectedCompoundExists = indexOps.getIndexInfo().stream()
                .anyMatch(indexInfo -> {
                    var fields = indexInfo.getIndexFields();
                    return fields.size() == 2
                            && "trip_id".equals(fields.get(0).getKey())
                            && "is_selected".equals(fields.get(1).getKey());
                });

        if (!tripSelectedCompoundExists) {
            indexOps.createIndex(new CompoundIndexDefinition(new Document("trip_id", 1).append("is_selected", 1)));
            log.info("Created compound index on 'trip_id' + 'is_selected' for Route collection");
        } else {
            log.info("Compound index on 'trip_id' + 'is_selected' already exists for Route collection");
        }
    }
}
