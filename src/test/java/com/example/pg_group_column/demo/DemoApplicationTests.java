package com.example.pg_group_column.demo;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Test class that shows examples of the:
 * <pre>
 *     ERROR: column "$COLUMN" must appear in the GROUP BY clause or be used in an aggregate function
 * </pre>
 * exception when grouping on a function with a Postgres Backend.
 * <p></p>
 * If desired, one can put a breakpoint just before the <code>repository.find...()</code> and <code>query.getResultList()</code>
 * and debug inside the docker container with the following commands:
 *
 * <pre>
 *     $ CONTAINER_ID=$(docker ps | grep jpa_exception_demo | cut -d' ' -f 1); docker exec -it $CONTAINER_ID bash -c 'su postgres'
 *     $ psql -U test
 *     $ SELECT entity_id, date_trunc('day', ts AT TIME ZONE 'UTC' - (INTERVAL '1 hour') * 4) AS ts, SUM(revenue) AS sum_revenue FROM demo.entity_revenue WHERE entity_id = 'b6689c85-8aab-4e3d-8803-3283dba46457' AND ts AT TIME ZONE 'UTC' >= '2024-07-01T00:00:00Z' AND ts AT TIME ZONE 'UTC' < '2024-07-08T00:00:00Z' GROUP BY entity_id, date_trunc('day', ts AT TIME ZONE 'UTC' - (INTERVAL '1 hour') * 4) ORDER BY 2 ASC, 3 ASC;
 * </pre>
 *
 * The query will complete sucessfully in psql.
 */
// Can be commented out, only used so that Data can be inspected with psql while debugging.
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class DemoApplicationTests extends PostgresContainerConfiguration {

    public static final ZoneId ZONE = ZoneOffset.UTC;
    private final Random rng = new Random();
    @Autowired
    private DemoEntityRepository repository;

    @Autowired
    private EntityManager em;

    private final Instant from = ZonedDateTime.of(2024, 7, 1, 0, 0, 0, 0, ZONE).toInstant();
    private final Instant until = ZonedDateTime.of(2024, 7, 8, 0, 0, 0, 0, ZONE).toInstant();
    private final UUID entityId = UUID.fromString("b6689c85-8aab-4e3d-8803-3283dba46457");
    private final UUID anotherEntity = UUID.fromString("38483b20-f552-4cf8-aa7f-13dc455c67e3");

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @BeforeEach
    void setup() {
        repository.deleteAll();
        insertTestData(entityId);
        insertTestData(anotherEntity);
        log.info("Setup data in PG running in Container {} (id: {})", db.getContainerName(), db.getContainerId());
    }

    @Test
    @DisplayName("ERROR: column \"hour\" does not exist when grouping on function definition")
    void repositoryIssue() {
        repository.findByIdAndHourBetweenGroupedByDay(entityId, from, until, 4);
    }

    @Test
    @DisplayName("Same as above using EntityManager#creatNativeQuery")
    void usingEntityManager() {
        var query = em.createNativeQuery(DemoEntityRepository.QUERY)
                .setParameter("entityId", entityId)
                .setParameter("from", from)
                .setParameter("until", until)
                .setParameter("offset", 4);
        query.getResultList();
    }

    private void insertTestData(UUID entityId) {
        // for each day
        List<DemoEntity> entities = IntStream.range(1, 8)
                .boxed()
                .flatMap(idx -> {
                    // for each hour in day
                    return IntStream.range(0, 23).boxed().map(hour -> {
                        int minutes = rng.nextInt(0, 60);
                        Instant timestamp = ZonedDateTime.of(2024, 7, idx, hour, minutes, 0, 0, ZONE).toInstant();
                        BigDecimal value = BigDecimal.valueOf(rng.nextLong(1, 100));
                        return new DemoEntity(entityId, timestamp, value);
                    });
                }).toList();
        repository.saveAllAndFlush(entities);
    }

}
