package io.peanut.routing;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Factory class for creating instances of {@link HttpRouter}.
 *
 * <p>This class provides convenient static methods to construct and configure HTTP routers
 * in a fluent and type-safe manner. Instantiation of this class is not allowed.
 */
public final class HttpRouterFactory
{
    private HttpRouterFactory()
    {
        throw new UnsupportedOperationException("Instantiation Restricted");
    }

    /**
     * Creates and configures a new {@link HttpRouter} instance using the provided configuration lambda.
     *
     * <p>This method allows inline and fluent configuration of routes:
     *
     * <pre>{@code
     * HttpRouter<Handler> router = HttpRouterFactory.create(config -> {
     *     config.add("/api/v1/users", new UsersHandler());
     *     config.add("/api/v1/posts/:id", new PostHandler());
     * });
     * }</pre>
     *
     * @param config a {@link Consumer} that accepts a {@link HttpRouterConfiguration} to define routes
     * @param <T>          the type of the route handler
     * @return a configured {@code HttpRouter} instance
     * @throws NullPointerException if {@code config} is {@code null}
     */
    public static <T> HttpRouter<T> create(Consumer<HttpRouterConfiguration<T>> config)
    {
        Objects.requireNonNull(config, "'config' cannot be null");

        HttpRouterConfiguration<T> configuration = new HttpRouterConfiguration<>();
        config.accept(configuration);

        return new HttpRouter<>(configuration);
    }

    /**
     * Creates a new {@link HttpRouter} using an already prepared configuration.
     *
     * <p>This method is useful when route configuration is built externally or reused across components.
     * You can define routes in advance and then pass the resulting {@link HttpRouterConfiguration} instance
     * directly to the router factory.
     *
     * <p>Example usage:
     *
     * <pre>{@code
     * RouterConfiguration<Handler> config = new RouterConfiguration<>();
     * config.add("/api/v1/internal/health", new HealthHandler());
     * config.add("/api/v1/internal/status", new StatusHandler());
     *
     * HttpRouter<Handler> router = HttpRouterFactory.create(config);
     * }</pre>
     *
     * @param configuration the prepared {@link HttpRouterConfiguration} containing routes
     * @param <T>           the type of the route handler
     * @return a configured {@code HttpRouter} instance
     * @throws NullPointerException if {@code configuration} is {@code null}
     */
    public static <T> HttpRouter<T> create(HttpRouterConfiguration<T> configuration)
    {
        Objects.requireNonNull(configuration, "'configuration' cannot be null");

        return new HttpRouter<>(configuration);
    }
}