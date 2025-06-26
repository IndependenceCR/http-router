package io.peanut.routing;

import java.util.*;

public final class HttpRouter<T>
{
    private final Node<T> root;

    private HttpRouter()
    {
        throw new UnsupportedOperationException("Instantiation Restricted");
    }

    HttpRouter(HttpRouterConfiguration<T> configuration)
    {
        this.root = configuration.root;
    }

    public RouteResult<T> route(String requestPath)
    {
        Objects.requireNonNull(requestPath, "'requestPath' cannot be null or empty");

        Node<T> current = this.root;

        boolean isDelimiterLeading = requestPath.charAt(0) == '/';
        int startIndex = isDelimiterLeading ? 1 : 0;
        int targetLength = requestPath.length();

        RouteResult<T> routeResult = null;
        Map<String, String> parameters = Collections.emptyMap();

        for (int sOffset = startIndex, eOffset = requestPath.indexOf('/', startIndex);
             sOffset < targetLength;
             sOffset = eOffset + 1, eOffset = (sOffset <= targetLength ? requestPath.indexOf('/', sOffset) : -1))
        {
            if (eOffset < 0)
            {
                eOffset = targetLength;
            }

            Node<T> next = null;
            if (current.children != Node.EMPTY_CHILDREN)
            {
                next = NodeChooser.choose(current.children, sOffset, eOffset, requestPath);
            }

            if (Objects.nonNull(next))
            {
                if (next.isParameterized)
                {
                    if (parameters == Collections.EMPTY_MAP)
                    {
                        // Optimistic initial capacity:
                        // Most route patterns contain no more than 4 parameters (e.g. /users/{id}/posts/{postId}),
                        // so we preallocate a small map to avoid resizing in common cases.
                        parameters = new HashMap<>(4);
                    }
                    parameters.put(next.pathSegment, requestPath.substring(sOffset, eOffset));
                }

                boolean isLastPathSegment = (eOffset == targetLength);

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
                routeResult = new RouteResult<>(null, parameters);
                break;
            }
        }

        return routeResult;
    }
}