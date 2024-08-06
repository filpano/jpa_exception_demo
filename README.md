See: https://github.com/spring-projects/spring-data-jpa/issues/3430

Demo project that shows examples of the:
```
ERROR: column "$COLUMN" must appear in the GROUP BY clause or be used in an aggregate function
```
exception when grouping on a function with Spring JPA + a Postgres Backend.

If desired, one can put a breakpoint just before the <code>repository.find...()</code> and <code>query.getResultList()</code>
and debug inside the docker container with the following commands:

```
$ CONTAINER_ID=$(docker ps | grep jpa_exception_demo | cut -d' ' -f 1); docker exec -it $CONTAINER_ID bash -c 'su postgres'
$ psql -U test
$ SELECT entity_id, date_trunc('day', ts AT TIME ZONE 'UTC' - (INTERVAL '1 hour') * 4) AS ts, SUM(revenue) AS sum_revenue FROM demo.entity_revenue WHERE entity_id = 'b6689c85-8aab-4e3d-8803-3283dba46457' AND ts AT TIME ZONE 'UTC' >= '2024-07-01T00:00:00Z' AND ts AT TIME ZONE 'UTC' < '2024-07-08T00:00:00Z' GROUP BY entity_id, date_trunc('day', ts AT TIME ZONE 'UTC' - (INTERVAL '1 hour') * 4) ORDER BY 2 ASC, 3 ASC;
```

The query will complete sucessfully in psql.
