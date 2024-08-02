package com.example.pg_group_column.demo;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.ThreadLocalRandom;

@DataJpaTest(showSql = false)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = PostgresContainerConfiguration.DataSourceInitializer.class)
class PostgresContainerConfiguration {


	protected static PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
			.withCreateContainerCmdModifier(cmd -> cmd.withName("jpa_exception_demo-" + ThreadLocalRandom.current().nextInt()));

	static {
		db.start();
	}

	public static class DataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
					applicationContext,
					"spring.test.database.replace=none", // Tells Spring Boot not to start in-memory db for tests.
					"spring.datasource.url=" + db.getJdbcUrl(),
					"spring.datasource.username=" + db.getUsername(),
					"spring.datasource.password=" + db.getPassword()
			);
		}
	}
}
