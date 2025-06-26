package io.peanut.routing;

import java.util.Objects;

final class NodeChooser
{
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

    private static <T> Node<T> pickParameterized(Node<T>[] children)
    {
        Node<T> node = null;
        if (children.length > 0)
        {
            Node<T> tail = children[children.length - 1];
            if (tail.isParameterized)
            {
                node = tail;
            }
        }

        return node;
    }

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
