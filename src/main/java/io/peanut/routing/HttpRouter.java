package io.peanut.routing;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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
        Map<String, String> params = Collections.emptyMap();

        for (int start = startIndex, end = requestPath.indexOf('/', startIndex);
             start < targetLength;
             start = end + 1, end = (start <= targetLength ? requestPath.indexOf('/', start) : -1))
        {
            if (end < 0)
            {
                end = targetLength;
            }

            boolean isParameterized = requestPath.charAt(start) == ':';
            String pathSegment;

            if (isParameterized)
            {
                pathSegment = requestPath.substring(start + 1, end);
            } else
            {
                pathSegment = requestPath.substring(start, end);
            }

            Node<T> next = Node.binarySearch(current.children, pathSegment);
            boolean isLastPathSegment = end == targetLength;

            if (Objects.nonNull(next))
            {
                if (!isLastPathSegment)
                {
                    current = next;
                } else
                {
                    routeResult = new RouteResult<>(next.handler, params);
                    break;
                }
            } else
            {
                routeResult = new RouteResult<>(null, params);
                break;
            }
        }

        return routeResult;
    }
}