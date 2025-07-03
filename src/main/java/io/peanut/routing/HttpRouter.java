package io.peanut.routing;

import java.util.*;

/**
 * HttpRouter is a routing engine that matches HTTP request paths
 * to registered handler instances.
 *
 * <p>It uses a immutable tree of {@link Node} objects representing path segments
 * to efficiently resolve the most appropriate handler for a given
 * request path. It supports static and parameterized path segments.
 *
 * <p>Routing is performed by splitting the request path into segments
 * by '/' delimiter and traversing the tree to find the best match.
 * If a parameterized segment is matched, the parameter value is extracted
 * and returned as part of the {@link RouteResult}.
 *
 * @param <T> the type of handler associated with the routes
 */
public final class HttpRouter<T>
{
    private final Node<T> root;

    private HttpRouter()
    {
        throw new UnsupportedOperationException("Instantiation Restricted");
    }

    HttpRouter(HttpRouterConfiguration<T> configuration)
    {
        // In the future, with new parameters or configuration options,
        // this should be expanded to properly initialize all necessary state.
        this.root = configuration.root;
    }

    /**
     * Routes the given request path to a handler.
     *
     * <p>The path is split by '/' into segments, and the routing tree is
     * traversed to find a matching route. If a parameterized segment is matched,
     * its value is extracted into the returned {@link RouteResult}.
     *
     * <p>If no matching route is found, the handler in {@link RouteResult}
     * will be {@code null} and parameters map may be empty or partially populated.
     *
     * @param requestPath the HTTP request path to route (must not be null or empty)
     * @return a {@link RouteResult} containing the matched handler and parameters
     * @throws NullPointerException if {@code requestPath} is null
     */
    public RouteResult<T> route(String requestPath)
    {
        Objects.requireNonNull(requestPath, "'requestPath' cannot be null or empty");

        Node<T> current = this.root;

        int pathLength = requestPath.length();

        boolean isDelimiterLeading = pathLength > 1 && requestPath.charAt(0) == '/';
        boolean isDelimiterTrailing = requestPath.charAt(pathLength - 1) == '/';

        int startIndex = isDelimiterLeading ? 1 : 0;
        int endIndex = isDelimiterTrailing ? pathLength - 1 : pathLength;

        RouteResult<T> routeResult = null;
        Map<String, String> parameters = Collections.emptyMap();

        for (int sOffset = startIndex, eOffset = Node.indexOfDelimiter(requestPath, sOffset, endIndex);
             sOffset <= endIndex;
             sOffset = eOffset + 1, eOffset = Node.indexOfDelimiter(requestPath, sOffset, endIndex))
        {
            Node<T> next = NodeChooser.choose(current.children, sOffset, eOffset, requestPath);

            if (Objects.nonNull(next))
            {
                if (next.isParameterized)
                {
                    if (parameters == Collections.EMPTY_MAP)
                    {
                        // Optimistic initial capacity:
                        // Most route patterns contain no more than 3 parameters (e.g. /users/{id}/posts/{postId}),
                        // so we preallocate a small map to avoid resizing in common cases.
                        parameters = new HashMap<>(3);
                    }
                    parameters.put(next.pathSegment, requestPath.substring(sOffset, eOffset));
                }

                boolean isLastPathSegment = (eOffset == endIndex);

                if (!isLastPathSegment)
                {
                    current = next;
                } else
                {
                    routeResult = new RouteResult<>(next.handler, parameters);
                    break;
                }
            } else
            {
                routeResult = new RouteResult<>(null, Collections.emptyMap());
                break;
            }
        }

        return routeResult;
    }
}