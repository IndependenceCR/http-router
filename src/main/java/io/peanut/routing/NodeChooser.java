package io.peanut.routing;

import java.util.Objects;

/**
 * Utility class responsible for selecting the appropriate child {@link Node}
 * during routing path traversal.
 * <p>
 * This class handles both exact matches (via {@code regionMatches}) and fallback
 * to parameterized nodes when no exact match is found.
 * <p>
 * Designed for high-performance matching in HTTP router implementations.
 */
final class NodeChooser
{
    /**
     * Performs a linear search among the given child nodes to find a node whose
     * {@code pathSegment} matches the specified segment of the target path.
     *
     * @param children     array of child nodes to search
     * @param startOffset  start index (inclusive) of the path segment in {@code targetPath}
     * @param endOffset    end index (exclusive) of the path segment in {@code targetPath}
     * @param targetPath   full path to extract the segment from
     * @param <T>          handler type associated with the node
     * @return matching {@link Node} if found, or {@code null} otherwise
     */
    private static <T> Node<T> linearSearch(Node<T>[] children, int startOffset, int endOffset, String targetPath)
    {
        int segmentLength = endOffset - startOffset;

        for (Node<T> child : children)
        {
            String childPath = child.pathSegment;

            // Fast equality check that avoids some internal String checks,
            // improving performance by ~5% in hot paths by skipping certain safety checks in String.equals().
            boolean optimisticEquality = childPath.length() == segmentLength && childPath.charAt(0) == targetPath.charAt(startOffset);
            if (optimisticEquality && targetPath.regionMatches(startOffset, childPath, 0, segmentLength))
            {
                return child;
            }
        }

        return null;
    }

    /**
     * Selects a parameterized child node from the given children array, if one exists.
     * <p>
     * By convention, a parameterized node is expected to be the last element in the array.
     *
     * @param children array of child nodes
     * @param <T>      handler type associated with the node
     * @return parameterized {@link Node} if found, or {@code null} otherwise
     */
    private static <T> Node<T> pickParameterized(Node<T>[] children)
    {
        if (children.length == 0)
        {
            return null;
        }

        Node<T> tail = children[children.length - 1];
        return tail.isParameterized ? tail : null;
    }

    /**
     * Chooses the matching child node from the given list based on the provided path segment.
     * <p>
     * This method first attempts to find an exact match via {@link #linearSearch}, and if none is found,
     * falls back to {@link #pickParameterized}.
     *
     * @param children     array of child nodes
     * @param startOffset  start index (inclusive) of the path segment in {@code targetPath}
     * @param endOffset    end index (exclusive) of the path segment in {@code targetPath}
     * @param targetPath   full path from which to extract the segment
     * @param <T>          handler type associated with the node
     * @return a matching {@link Node}, or {@code null} if no match is found
     */
    public static <T> Node<T> choose(Node<T>[] children, int startOffset, int endOffset, String targetPath)
    {
        Node<T> node = NodeChooser.linearSearch(children, startOffset, endOffset, targetPath);

        if (Objects.isNull(node))
        {
            node = NodeChooser.pickParameterized(children);
        }

        return node;
    }
}
