package co.com.manager.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.r2dbc")
public record PostgresqlConnectionProperties(
        String host,
        Integer port,
        String database,
        String schema,
        String username,
        String password,
        PoolConfig pool) {

    public PostgresqlConnectionProperties {
        if (port == null) { port = 5432; }
        if (schema == null || schema.isEmpty()) { schema = "public"; }
    }

    public record PoolConfig(
            Integer initialSize,
            Integer maxSize,
            Integer maxIdleTime
    ) {
        public PoolConfig {
            if (initialSize == null) { initialSize = 12; }
            if (maxSize == null) { maxSize = 15; }
            if (maxIdleTime == null) { maxIdleTime = 30; }
        }
    }
}
