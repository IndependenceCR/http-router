package io.peanut.routing;

import java.util.Arrays;

final class Node<T>
{
    public static String ROOT_CLASSIFIER = "<root>";
    public static final Node<?>[] EMPTY_CHILDREN = new Node<?>[0];

    final String pathSegment;
    final boolean isParameterized;
    final Node<T>[] children;
    final T handler;

    public static <T> Node<T> rebuildAncestor(Node<T> currentAncestor, Node<T> current, Node<T> newNode)
    {
        if (currentAncestor == current)
        {
            Node<T>[] ancestorChildren = Node.insertChildrenOrdered(currentAncestor.children, newNode);
            return new Node<>(currentAncestor.pathSegment, currentAncestor.isParameterized, ancestorChildren, currentAncestor.handler);
        }

        Node<T>[] oldChildren = currentAncestor.children;
        Node<T>[] newChildren = Arrays.copyOf(oldChildren, oldChildren.length);

        boolean changed = false;
        for (int i = 0; i < oldChildren.length; i++)
        {
            Node<T> child = oldChildren[i];
            Node<T> updated = rebuildAncestor(child, current, newNode);
            if (updated != child)
            {
                newChildren[i] = updated;
                changed = true;
                break;
            }
        }

        if (!changed)
        {
            return currentAncestor;
        }

        return new Node<>(currentAncestor.pathSegment, currentAncestor.isParameterized, newChildren, currentAncestor.handler);
    }

    @SuppressWarnings("unchecked")
    private static <T> Node<T>[] insertChildrenOrdered(Node<T>[] oldChildren, Node<T> child)
    {
        int oldLength = oldChildren.length;
        Node<T>[] newArray = (Node<T>[]) new Node<?>[oldLength + 1];

        int insertIndex = 0;
        while (insertIndex < oldLength)
        {
            Node<T> current = oldChildren[insertIndex];

            if (current.isParameterized || child.pathSegment.compareTo(current.pathSegment) < 0) {
                break;
            }

            insertIndex++;
        }

        if (insertIndex > 0)
        {
            System.arraycopy(oldChildren, 0, newArray, 0, insertIndex);
        }

        newArray[insertIndex] = child;

        if (insertIndex < oldLength)
        {
            System.arraycopy(oldChildren, insertIndex, newArray, insertIndex + 1, oldLength - insertIndex);
        }

        return newArray;
    }

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
        return "TreeNode (path=" + this.pathSegment + ", children_count=" + this.children.length + ", handler=" + this.handler + ")";
    }
}
