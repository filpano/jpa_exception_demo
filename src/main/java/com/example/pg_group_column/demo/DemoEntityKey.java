package com.example.pg_group_column.demo;

import java.time.Instant;
import java.util.UUID;

public class DemoEntityKey {
    private UUID entityId;
    private Instant ts;
    public DemoEntityKey() {
    }

    public DemoEntityKey(UUID entityId, Instant ts) {
        this.entityId = entityId;
        this.ts = ts;
    }
}
