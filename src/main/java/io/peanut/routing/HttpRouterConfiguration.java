package io.peanut.routing;

import java.util.Objects;

public final class HttpRouterConfiguration<T>
{
    Node<T> root = new Node<>("<root>", false, Node.EMPTY_CHILDREN, null);

    private static <T> Node<T> addRoute(Node<T> root, String targetPath, T handler)
    {
        Node<T> current = root;
        Node<T> ancestor = root;

        boolean isDelimiterLeading = targetPath.charAt(0) == '/';
        int startIndex = isDelimiterLeading ? 1 : 0;
        int targetLength = targetPath.length();

        for (int start = startIndex, end = targetPath.indexOf('/', startIndex);
             start <= targetLength;
             start = end + 1, end = (start <= targetLength ? targetPath.indexOf('/', start) : -1))
        {
            if (end < 0)
            {
                end = targetLength;
            }

            boolean isParameterized = targetPath.charAt(start) == ':';
            String pathSegment;

            if (isParameterized)
            {
                pathSegment = targetPath.substring(start + 1, end);
            } else
            {
                pathSegment = targetPath.substring(start, end);
            }

            Node<T> next = Node.binarySearch(current.children, pathSegment);
            boolean isLastPathSegment = end == targetLength;

            if (Objects.isNull(next))
            {
                T currentHandler = isLastPathSegment ? handler : null;
                Node<T> candidate = new Node<>(pathSegment, isParameterized, Node.EMPTY_CHILDREN, currentHandler);

                ancestor = Node.rebuildAncestor(ancestor, current, candidate);
                current = candidate;
            } else
            {
                current = next;
            }
        }

        return ancestor;
    }

    public HttpRouterConfiguration<T> add(String targetPath, T handler)
    {
        this.root = HttpRouterConfiguration.addRoute(this.root, targetPath, handler);
        return this;
    }
}
