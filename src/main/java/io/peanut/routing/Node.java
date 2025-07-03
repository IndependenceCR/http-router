package io.peanut.routing;

import java.util.Arrays;

/**
 * Represents a node in the HTTP routing tree.
 *
 * <p>Each node corresponds to a segment of a path and may contain:
 * <ul>
 *   <li>a path segment string,</li>
 *   <li>a flag indicating if the segment is parameterized (e.g., ":id"),</li>
 *   <li>an array of child nodes representing subsequent path segments,</li>
 *   <li>and an optional handler associated with this route.</li>
 * </ul>
 *
 * <p><b>Children storage visualization:</b><br>
 * The {@code children} array is kept sorted lexicographically by {@code pathSegment}.
 * Parameterized child nodes (e.g. representing "{id}") are always stored
 * in the <i>last</i> position of the array if present.<br>
 *
 * For example, for children array with these pathSegments:
 * <pre>
 *   ["about", "contact", "user", "{param}"]
 *   ^          ^          ^       ^
 *   |          |          |       |
 *   lex sorted              parameterized node always last
 * </pre>
 *
 * <p>This ordering facilitates efficient searching with optimizations
 * such as binary search on the non-parameterized children.
 *
 * <p>Nodes are immutable once created. Modifications to the tree are done
 * by creating new nodes with updated children arrays.
 *
 * @param <T> the type of the handler associated with the route node
 */
final class Node<T>
{
    /**
     * Special classifier string for the root node.
     */
    public static final String ROOT_CLASSIFIER = "<root>";
    /**
     * Empty children array shared as a constant to avoid unnecessary allocations.
     */
    public static final Node<?>[] EMPTY_CHILDREN = new Node<?>[0];

    final String pathSegment;
    final boolean isParameterized;
    final Node<T>[] children;
    final T handler;

    /**
     * Finds the next '/' character in the given {@code path} starting from {@code startOffset}.
     * If '/' is not found, returns the {@code defaultOffset} instead.
     *
     * @param path the string to search in
     * @param startOffset the position to start searching from
     * @param defaultOffset the value to return if '/' is not found
     * @return the index of the next '/' after {@code startOffset}, or {@code defaultOffset} if none found
     */
    static int indexOfDelimiter(String path, int startOffset, int defaultOffset)
    {
        int currentIndex = path.indexOf('/', startOffset);
        return currentIndex != -1 ? currentIndex : defaultOffset;
    }

    /**
     * Rebuilds the ancestor node in the routing tree with a replacement node inserted
     * in place of the target node.
     *
     * <p>This method recursively walks down the tree from the ancestor node until it
     * reaches the target node, then inserts the replacement node in order among the children.
     * It creates new nodes on the path back to maintain immutability.
     *
     * @param currentAncestor the current ancestor node to rebuild
     * @param current the target node to replace
     * @param newNode the replacement node to insert
     * @param <T> the handler type
     * @return a new node with the replacement inserted, or the original ancestor if no changes were made
     */
    static <T> Node<T> rebuildAncestor(Node<T> currentAncestor, Node<T> current, Node<T> newNode)
    {
        if (currentAncestor == current)
        {
            Node<T>[] ancestorChildren = Node.insertChildrenOrdered(currentAncestor.children, newNode);
            return new Node<>(currentAncestor.pathSegment, currentAncestor.isParameterized, ancestorChildren, currentAncestor.handler);
        }

        Node<T>[] oldChildren = currentAncestor.children;
        Node<T>[] newChildren = Arrays.copyOf(oldChildren, oldChildren.length);

        boolean isChildModified = false;
        for (int offset = 0; offset < oldChildren.length; offset++)
        {
            Node<T> child = oldChildren[offset];
            Node<T> updated = rebuildAncestor(child, current, newNode);
            if (updated != child)
            {
                newChildren[offset] = updated;
                isChildModified = true;
                break;
            }
        }

        if (!isChildModified)
        {
            return currentAncestor;
        }

        return new Node<>(currentAncestor.pathSegment, currentAncestor.isParameterized, newChildren, currentAncestor.handler);
    }

    /**
     * Inserts a new child node into an existing array of children while preserving order.
     *
     * <p>Parameterized nodes always come last, and nodes are sorted lexicographically by their path segment.
     *
     * @param oldChildren the existing children array
     * @param child the new child node to insert
     * @param <T> the handler type
     * @return a new array containing the old children plus the inserted child in correct order
     */
    @SuppressWarnings("unchecked")
    static <T> Node<T>[] insertChildrenOrdered(Node<T>[] oldChildren, Node<T> child)
    {
        int oldLength = oldChildren.length;
        Node<T>[] newArray = (Node<T>[]) new Node<?>[oldLength + 1];

        int insertOffset = 0;
        for (; insertOffset < oldLength; insertOffset++)
        {
            Node<T> current = oldChildren[insertOffset];
            if (current.isParameterized || child.pathSegment.compareTo(current.pathSegment) < 0)
            {
                break;
            }
        }

        if (insertOffset > 0)
        {
            System.arraycopy(oldChildren, 0, newArray, 0, insertOffset);
        }

        newArray[insertOffset] = child;

        if (insertOffset < oldLength)
        {
            System.arraycopy(oldChildren, insertOffset, newArray, insertOffset + 1, oldLength - insertOffset);
        }

        return newArray;
    }

    /**
     * Constructs a new routing tree node.
     *
     * @param pathSegment the path segment string this node represents
     * @param isParameterized true if this path segment is parameterized
     * @param children the child nodes of this node
     * @param handler the handler associated with this node, may be null
     */
    @SuppressWarnings("unchecked")
    public Node(String pathSegment, boolean isParameterized, Node<?>[] children, T handler)
    {
        this.pathSegment = pathSegment;
        this.isParameterized = isParameterized;
        this.children = (Node<T>[]) children;
        this.handler = handler;
    }

    @Override
    public String toString()
    {
        return "Node (path=" + this.pathSegment + ", children_count=" + this.children.length + ", handler=" + this.handler + ")";
    }
}
