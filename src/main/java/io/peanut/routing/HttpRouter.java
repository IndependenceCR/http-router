package io.peanut.routing;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpRouter<T>
{
    private final Node<T> root;

    private HttpRouter()
    {
        throw new UnsupportedOperationException("Instantiation Restricted");
    }

    HttpRouter(RouterConfiguration<T> configuration)
    {
        this.root = configuration.getRootUnsafe();
    }

    public RouteResult<T> route(String requestPath)
    {
        ObjectUtil.checkStringIsNotEmpty(requestPath, "'requestPath' cannot be null or empty");

        RouteResult<T> routeResult = null;

        Node<T> current = root;
        boolean isDelimiterLeading = requestPath.charAt(0) == '/';
        int requestLength = requestPath.length();

        Map<String, String> currentParams = Collections.emptyMap();

        for (int offset = isDelimiterLeading ? 1 : 0, nextDelimiter = requestPath.indexOf('/', offset);
             offset <= requestLength;
             offset = nextDelimiter + 1, nextDelimiter = requestPath.indexOf('/', offset))
        {
            if (nextDelimiter == -1)
            {
                nextDelimiter = requestLength;
            }

            String pathSegment = requestPath.substring(offset, nextDelimiter);

            Node<T> next = current.findAnyMatch(pathSegment);

            if (Objects.nonNull(next))
            {
                if (nextDelimiter != requestLength)
                {
                    if (next.isParameterized())
                    {
                        if (currentParams == Collections.EMPTY_MAP)
                        {
                            currentParams = new LinkedHashMap<>();
                        }
                        currentParams.put(Node.toReadablePathSegment(next), pathSegment);
                    }
                    current = next;
                } else
                {
                    if (next.isParameterized())
                    {
                        if (currentParams == Collections.EMPTY_MAP)
                        {
                            currentParams = new LinkedHashMap<>();
                        }
                        currentParams.put(Node.toReadablePathSegment(next), pathSegment);
                    }

                    routeResult = new RouteResult<>(next.handler(), currentParams);
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