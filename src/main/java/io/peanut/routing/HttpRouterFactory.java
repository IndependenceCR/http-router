package io.peanut.routing;

import java.util.function.Consumer;

public final class HttpRouterFactory
{
    private HttpRouterFactory()
    {
        throw new UnsupportedOperationException("Instantiation Restricted");
    }

    public static <T> HttpRouter<T> createRouter(Consumer<RouterConfiguration<T>> configurable)
    {
        ObjectUtil.checkNotNull(configurable, "'configurable' cannot be null");

        RouterConfiguration<T> configuration = new RouterConfiguration<>();
        configurable.accept(configuration);

        return new HttpRouter<>(configuration);
    }

    public static <T> HttpRouter<T> createRouter(RouterConfiguration<T> configuration)
    {
        ObjectUtil.checkNotNull(configuration, "'configuration' cannot be null");

        return new HttpRouter<>(configuration);
    }
}