package io.peanut.routing;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final class Node<T>
{
    public static final String ROOT_CLASSIFIER = "<root>";

    private List<Node<T>> children = Collections.emptyList();
    private final String pathSegment;
    private final boolean isParameterized;
    private final T handler;

    private static <T> boolean compareNodesPathSegmentEquality(Node<T> child, Node<T> node)
    {
        return child.pathSegment.length() == node.pathSegment.length() &&
                child.pathSegment.charAt(0) == node.pathSegment.charAt(0) &&
                child.pathSegment.equals(node.pathSegment);
    }

    private static <T> boolean comparePathSegmentEquality(Node<T> child, String pathSegment)
    {
        return child.pathSegment.length() == pathSegment.length() &&
                child.pathSegment.charAt(0) == pathSegment.charAt(0) &&
                child.pathSegment.equals(pathSegment);
    }

    public Node(String pathSegment, boolean isParameterized, T handler)
    {
        this.pathSegment = pathSegment;
        this.isParameterized = isParameterized;
        this.handler = handler;
    }

    public Node<T> findExactMatch(String pathSegment, boolean isParameterized)
    {
        if (!isParameterized)
        {
            for (Node<T> child : children)
            {
                if (!child.isParameterized && Node.comparePathSegmentEquality(child, pathSegment))
                {
                    return child;
                }
            }
        } else
        {
            for (Node<T> child : children)
            {
                if (child.isParameterized)
                {
                    return child;
                }
            }
        }

        return null;
    }

    public Node<T> findAnyMatch(String pathSegment)
    {
        for (Node<T> child : children)
        {
            if (!child.isParameterized && Node.comparePathSegmentEquality(child, pathSegment))
            {
                return child;
            }
        }

        for (Node<T> child : children)
        {
            if (child.isParameterized)
            {
                return child;
            }
        }

        return null;
    }

    public Node<T> write(Node<T> node)
    {
        for (Node<T> child : children)
        {
            if (child.isParameterized == node.isParameterized && Node.compareNodesPathSegmentEquality(child, node))
            {
                return child;
            }
        }

        if (children.isEmpty())
        {
            children = new LinkedList<>();
        }

        children.add(node);

        return node;
    }

    public T handler()
    {
        return handler;
    }

    public boolean isParameterized()

    {
        return isParameterized;
    }

    public static <T> String toReadablePathSegment(Node<T> node)
    {
        return node.pathSegment.charAt(0) == ':' ? node.pathSegment.substring(1) : node.pathSegment;
    }

    @Override
    public String toString()
    {
        return "Node(path=" + this.pathSegment + ", children_count=" + this.children.size() + ", handler=" + this.handler + ")";
    }
}