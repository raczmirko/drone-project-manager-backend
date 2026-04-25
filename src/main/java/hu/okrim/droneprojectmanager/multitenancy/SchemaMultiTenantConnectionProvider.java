package hu.okrim.droneprojectmanager.multitenancy;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Provides connection management for a multi-tenant database setup using schema-based isolation.
 * <p>
 * This class implements the {@link MultiTenantConnectionProvider} interface to enable connections
 * for different tenants, identified by schemas. It also provides mechanisms to customize Hibernate
 * properties for supporting multi-tenancy.
 * <p>
 * The provider works by dynamically setting the search_path to the schema corresponding to the tenant
 * identifier for each database connection. If no tenant identifier is specified or is invalid, it defaults
 * to the preconfigured schema.
 * <p>
 * Key responsibilities:
 * - Acquire connections for any schema or a specific tenant schema.
 * - Release acquired connections, ensuring schema reset to the default state.
 * - Customize Hibernate properties for enabling multi-tenancy.
 * <p>
 * Default behaviors:
 * - Default schema is set to "public".
 * - Aggressive connection release is not supported.
 * <p>
 * Exceptions:
 * - Throws {@link HibernateException} when setting the schema fails.
 * - Throws {@link UnsupportedOperationException} if unwrapping unsupported types is attempted.
 * <p>
 * Usage of this provider integrates with Hibernate's multi-tenancy capability where schema-based
 * separation of tenant data is required.
 *
 * @see <a href="https://www.baeldung.com/hibernate-6-multitenancy">Baeldung - Hibernate 6 Multitenancy</a>
 */
@Component
public class SchemaMultiTenantConnectionProvider
        implements MultiTenantConnectionProvider<String>, HibernatePropertiesCustomizer {

    private static final String DEFAULT_SCHEMA = "public";

    private final DataSource dataSource;

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = getAnyConnection();
        String schema = normalizeSchema(tenantIdentifier);

        try (Statement statement = connection.createStatement()) {
            statement.execute("SET search_path TO \"" + schema + "\"");
        } catch (SQLException ex) {
            connection.close();
            throw new HibernateException(
                    "Could not set search_path to tenant " + schema,
                    ex
            );
        }

        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET search_path TO \"" + DEFAULT_SCHEMA + "\"");
        } finally {
            connection.close();
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(
                AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER,
                this
        );
    }

    private String normalizeSchema(String tenantIdentifier) {
        if (tenantIdentifier == null || tenantIdentifier.isBlank()) {
            return DEFAULT_SCHEMA;
        }
        return tenantIdentifier;
    }
}

