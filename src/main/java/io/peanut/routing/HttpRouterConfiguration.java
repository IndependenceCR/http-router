package io.peanut.routing;

import java.util.Objects;

/**
 * HTTP router configuration containing the root node of the route tree.
 * <p>
 * Allows adding routes with handlers and automatically builds a tree of path segments.
 * Paths may include parameterized segments (starting with ':').
 * </p>
 *
 * @param <T> the type of handler associated with a route
 */
public final class HttpRouterConfiguration<T>
{
    Node<T> root = new Node<>(Node.ROOT_CLASSIFIER, false, Node.EMPTY_CHILDREN, null);

    /**
     * Recursively adds a new route to the node tree.
     *
     * @param root the root node of the current route tree
     * @param targetPath the route path string, e.g. "/user/:id/profile"
     * @param handler the handler associated with the route (assigned to the last segment)
     * @return the updated root node of the tree with the added route
     */
    private static <T> Node<T> addRoute(Node<T> root, String targetPath, T handler)
    {
        Node<T> current = root;
        Node<T> ancestor = root;

        int pathLength = targetPath.length();

        boolean isDelimiterLeading = pathLength > 1 && targetPath.charAt(0) == '/';
        boolean isDelimiterTrailing = targetPath.charAt(pathLength - 1) == '/';

        int startIndex = isDelimiterLeading ? 1 : 0;
        int endIndex = isDelimiterTrailing ? pathLength - 1 : pathLength;

        for (int sOffset = startIndex, eOffset = Node.pathIndexOf(targetPath, sOffset, endIndex);
             sOffset <= endIndex;
             sOffset = eOffset + 1, eOffset = Node.pathIndexOf(targetPath, sOffset, endIndex))
        {
            boolean isParameterized = targetPath.charAt(sOffset) == ':';

            Node<T> next = NodeChooser.choose(current.children, sOffset, eOffset, targetPath);
            boolean isLastPathSegment = (eOffset == endIndex);

            if (Objects.isNull(next))
            {
                T currentHandler = isLastPathSegment ? handler : null;
                String pathSegment = targetPath.substring(isParameterized ? sOffset + 1 : sOffset, eOffset);
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

    /**
     * Validates the path string by checking for disallowed characters according to RFC 3986.
     * According to the RFC, certain characters are reserved or unsafe within URI paths and must be percent-encoded.
     * This method ensures that the path does not contain such characters un-encoded.
     * <p>
     * Disallowed characters: {@code < > # % { } | \ ^ [ ] `}.
     *
     * @param path the path to validate
     * @throws IllegalArgumentException if the path contains invalid characters
     */
    private static void validatePath(String path)
    {
        for (int offset = 0; offset < path.length(); offset++)
        {
            char ch = path.charAt(offset);
            switch (ch)
            {
                case '<': case '>': case '#': case '%': case '{': case '}':
                case '|': case '\\': case '^': case '[': case ']': case '`': case ' ':
                    throw new IllegalArgumentException("Unable to proceed invalid path '" + path + "'");
            }
        }
    }

    /**
     * Adds a new route with a handler to the router configuration.
     * <p>
     * The path must be valid and not contain disallowed characters.
     * The handler will be associated with the last segment of the path.
     * </p>
     *
     * @param targetPath the route path, e.g. "/user/:id"
     * @param handler the handler for the route
     * @return this configuration instance for method chaining
     * @throws NullPointerException if the path or handler is {@code null}
     * @throws IllegalArgumentException if the path contains disallowed characters
     */
    public HttpRouterConfiguration<T> add(String targetPath, T handler)
    {
        Objects.requireNonNull(targetPath, "'targetPath' cannot be null");
        Objects.requireNonNull(handler, "'handler' cannot be null");

        HttpRouterConfiguration.validatePath(targetPath);

        this.root = HttpRouterConfiguration.addRoute(this.root, targetPath, handler);
        return this;
    }
}
