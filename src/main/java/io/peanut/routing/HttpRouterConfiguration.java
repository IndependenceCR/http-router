package io.peanut.routing;

import java.util.Objects;

public final class HttpRouterConfiguration<T>
{
    Node<T> root = new Node<>(Node.ROOT_CLASSIFIER, false, Node.EMPTY_CHILDREN, null);

    private static <T> Node<T> addRoute(Node<T> root, String targetPath, T handler)
    {
        Node<T> current = root;
        Node<T> ancestor = root;

        int firstLeadingIndex = targetPath.charAt(0) == '/' ? 1 : 0;
        int targetLength = targetPath.length();

        for (int startIndex = firstLeadingIndex, endIndex = targetPath.indexOf('/', firstLeadingIndex);
             startIndex <= targetLength;
             startIndex = endIndex + 1, endIndex = (startIndex <= targetLength ? targetPath.indexOf('/', startIndex) : -1))
        {
            if (endIndex < 0)
            {
                endIndex = targetLength;
            }

            boolean isParameterized = targetPath.charAt(startIndex) == ':';

            Node<T> next = NodeChooser.choose(current.children, startIndex, endIndex, targetPath);
            boolean isLastPathSegment = endIndex == targetLength;

            if (Objects.isNull(next))
            {
                T currentHandler = isLastPathSegment ? handler : null;
                String pathSegment = targetPath.substring(isParameterized ? startIndex + 1 : startIndex, endIndex);
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
