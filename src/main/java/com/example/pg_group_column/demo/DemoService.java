package com.example.pg_group_column.demo;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DemoService {

    private final DemoEntityRepository repository;

    public DemoService(DemoEntityRepository repository) {
        this.repository = repository;
    }

    public List<DemoEntity> findAllEntitiesInTimespan(UUID entityId, Instant from, Instant until) {
        return repository.findByIdAndHourBetweenGroupedByDay(entityId, from, until, 4);
    }
}
