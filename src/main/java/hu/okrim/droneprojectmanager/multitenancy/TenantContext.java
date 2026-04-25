package hu.okrim.droneprojectmanager.multitenancy;

/**
 * Represents a thread-local context for storing and managing the current tenant identifier.
 * <p>
 * This utility class is typically used in multi-tenant applications where the current tenant
 * identifier needs to be stored and accessed in a thread-safe manner for the duration of a
 * request or operation. TenantContext provides methods to set, retrieve, and clear the
 * tenant identifier for the current thread.
 * <p>
 * The tenant context is especially useful for managing schema-based multi-tenancy in
 * database interactions. For example, this class can be used in conjunction with filters
 * or resolvers to set the tenant identifier based on the current request.
 * <p>
 * Key functionality:
 * - Stores the tenant identifier in a thread-local variable.
 * - Provides methods to set the tenant identifier for the current thread.
 * - Retrieves the tenant identifier for database operations or other tenant-specific logic.
 * - Clears the tenant identifier to avoid cross-thread contamination.
 * <p>
 * Thread-Safety:
 * - The usage of {@link ThreadLocal} ensures the tenant identifier is isolated to the
 *   current thread, preventing interference between concurrent threads.
 */
public final class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    public static String getTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
