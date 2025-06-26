package io.peanut.routing;

import java.util.Map;

/**
 * Represents the result of routing a request path to a handler.
 *
 * <p>This class encapsulates the handler matched for a given path,
 * along with a map of path parameters extracted during routing.
 *
 * @param <T> the type of the handler associated with the matched route
 */
public final class RouteResult<T>
{
    private final Map<String, String> parameters;
    private final T handler;

    /**
     * Creates a new RouteResult with the given handler and parameters.
     *
     * @param handler the handler matched for the route, may be null if no match
     * @param parameters the map of parameter names to values, never null;
     *                   either {@link java.util.Collections#EMPTY_MAP} or a mutable map
     */
    RouteResult(T handler, Map<String, String> parameters)
    {
        this.handler = handler;
        this.parameters = parameters;
    }

    public Map<String, String> getParameters()
    {
        return parameters;
    }

    public T getHandler()
    {
        return handler;
    }
}