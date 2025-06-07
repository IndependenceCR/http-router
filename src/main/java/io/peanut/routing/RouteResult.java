package io.peanut.routing;

import java.util.Map;

public final class RouteResult<T>
{
    private final Map<String, String> params;
    private final T handler;

    RouteResult(T handler, Map<String, String> params)
    {
        this.handler = handler;
        this.params = params;
    }

    public Map<String, String> params()
    {
        return params;
    }

    public T handler()
    {
        return handler;
    }
}