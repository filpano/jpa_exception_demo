package com.example.pg_group_column.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface DemoEntityRepository extends JpaRepository<DemoEntity, UUID> {

    String QUERY = """
            SELECT entity_id, date_trunc('day', ts AT TIME ZONE 'UTC' - (INTERVAL '1 hour') * :offset) AS ts, SUM(revenue) AS sum_revenue
            FROM demo.entity_revenue
            WHERE entity_id = :entityId AND ts AT TIME ZONE 'UTC' >= :from AND ts AT TIME ZONE 'UTC' < :until
            GROUP BY entity_id, date_trunc('day', ts AT TIME ZONE 'UTC' - (INTERVAL '1 hour') * :offset)
            ORDER BY 2 ASC, 3 ASC;
            """;

    @Query(value = QUERY, nativeQuery = true)
    List<DemoEntity> findByIdAndHourBetweenGroupedByDay(@Param("entityId") UUID entityId,
                                                          @Param("from") Instant from,
                                                          @Param("until") Instant until,
                                                          @Param("offset") int offset);

}
