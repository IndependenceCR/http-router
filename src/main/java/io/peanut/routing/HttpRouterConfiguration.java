package io.peanut.routing;

import java.util.Objects;

/**
 * HTTP router configuration containing the root node of the route tree.
 * <p>
 * Allows adding routes with handlers and automatically builds a tree of path segments.
 * Paths may include parameterized segments (starting with ':').
 * </p>
 *
 * <h2>Path normalization and routing rules</h2>
 * <ul>
 *   <li><b>Leading and trailing slashes:</b> Both are optional and do not affect routing.
 *       <br>
 *       For example, all of the following are considered equivalent:
 *       <ul>
 *         <li><code>/api/v1/readiness</code></li>
 *         <li><code>api/v1/readiness</code></li>
 *         <li><code>/api/v1/readiness/</code></li>
 *         <li><code>api/v1/readiness/</code></li>
 *       </ul>
 *   </li>
 *
 *   <li><b>Empty segments:</b> Consecutive slashes are allowed. No normalization is performed to collapse them.
 *       <br>
 *       These are all valid and treated literally:
 *       <ul>
 *         <li><code>//</code></li>
 *         <li><code>/a//b</code></li>
 *         <li><code>/user///profile</code></li>
 *       </ul>
 *       Be careful, as routes must match the exact structure — a route <code>/a/b</code> will not match <code>/a//b</code>.
 *   </li>
 *
 *   <li><b>Parameter syntax:</b> A path segment starting with a colon (e.g. <code>:id</code>, <code>:ray_id</code>)
 *       is treated as a parameter and matches any non-empty segment in that position.
 *       <br>
 *       For example, all of the following are valid:
 *       <ul>
 *         <li><code>/user/:user_id</code></li>
 *         <li><code>/user/:user_id/profile</code></li>
 *         <li><code>/user/:user_id/settings</code></li>
 *       </ul>
 *       <strong>Only one parameter segment is allowed per level</strong>; defining multiple parameter names under the same
 *       parent path is not allowed.
 *   </li>
 * </ul>
 *
 * <p>
 * Users should ensure consistent structure in route definitions. Although slashes and empty segments are flexible,
 * careless use may lead to ambiguous or unintended matches.
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

        for (int sOffset = startIndex, eOffset = Node.indexOfDelimiter(targetPath, sOffset, endIndex);
             sOffset <= endIndex;
             sOffset = eOffset + 1, eOffset = Node.indexOfDelimiter(targetPath, sOffset, endIndex))
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
