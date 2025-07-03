package io.peanut.routing;

import java.util.Objects;

/**
 * Helper class used to find the required child {@link Node} while matching a path.
 * @see Node
 */
final class NodeChooser
{
    /**
     * Performs a linear search through the child nodes to locate a {@link Node}
     * whose {@code pathSegment} matches the selected segment of the target path.
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
        boolean isPopulatedSegment = (segmentLength != 0);

        for (Node<T> child : children)
        {
            String childPath = child.pathSegment;
            int childPathLength = childPath.length();
            boolean optimisticEquality;

            if (isPopulatedSegment)
            {
                optimisticEquality = childPathLength == segmentLength && childPath.charAt(0) == targetPath.charAt(startOffset);
            } else
            {
                optimisticEquality = (childPathLength == 0);
            }

            // Fast equality check that avoids some internal String checks,
            // improving performance by ~5% in hot paths by skipping certain safety checks in String.equals().
            if (optimisticEquality && targetPath.regionMatches(startOffset, childPath, 0, segmentLength))
            {
                return child;
            }
        }

        return null;
    }

    /**
     * Selects a parameterized child node from the given children array, if one exists.
     *
     * @apiNote Parameterized nodes are conventionally stored as the last element in the array.
     * @see Node#insertChildrenOrdered(Node[], Node)
     *
     * @param children array of child nodes
     * @param <T>      handler type associated with the node
     * @return parameterized {@link Node} if found, or {@code null} otherwise
     */
    private static <T> Node<T> pickParameterized(Node<T>[] children)
    {
        Node<T> tail = children[children.length - 1];
        return tail.isParameterized ? tail : null;
    }

    /**
     * Finds the matching child {@link Node} in children list by specified segment of the path.
     *
     * @apiNote Parameterized nodes are conventionally stored as the last element in the array.
     * @see Node#insertChildrenOrdered(Node[], Node)
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
        if (children == Node.EMPTY_CHILDREN)
        {
            return null;
        }

        Node<T> node = NodeChooser.linearSearch(children, startOffset, endOffset, targetPath);

        if (Objects.isNull(node))
        {
            node = NodeChooser.pickParameterized(children);
        }

        return node;
    }
}
