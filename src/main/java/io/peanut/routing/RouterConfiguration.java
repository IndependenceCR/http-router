package io.peanut.routing;

import java.util.Objects;

public final class RouterConfiguration<T>
{
    private final Node<T> root = new Node<>(Node.ROOT_CLASSIFIER, false, null);

    public RouterConfiguration<T> add(String targetPath, T handler)
    {
        ObjectUtil.checkStringIsNotEmpty(targetPath, "'targetPath' cannot be empty or null");
        ObjectUtil.checkNotNull(handler, "'handler' cannot be null");

        Node<T> current = root;
        boolean isDelimiterLeading = targetPath.charAt(0) == '/';
        int targetLength = targetPath.length();

        for (int offset = isDelimiterLeading ? 1 : 0, nextDelimiter = targetPath.indexOf('/', offset);
             offset <= targetLength;
             offset = nextDelimiter + 1, nextDelimiter = targetPath.indexOf('/', offset))
        {
            if (nextDelimiter == -1)
            {
                nextDelimiter = targetLength;
            }

            String pathSegment = targetPath.substring(offset, nextDelimiter);
            boolean isParameterized = !pathSegment.isEmpty() && pathSegment.charAt(0) == ':';

            Node<T> next = current.findExactMatch(pathSegment, isParameterized);

            if (Objects.isNull(next))
            {
                if (nextDelimiter == targetLength)
                {
                    next = current.write(new Node<>(pathSegment, isParameterized, handler));
                } else
                {
                    next = current.write(new Node<>(pathSegment, isParameterized, null));
                }
            }

            current = next;
        }

        return this;
    }

    Node<T> getRootUnsafe()
    {
        return root;
    }
}
