package io.peanut.routing;

final class ObjectUtil
{
    public static <T> T checkNotNull(T arg, String message)
    {
        if (arg == null)
        {
            throw new NullPointerException(message);
        }

        return arg;
    }

    public static boolean checkStringIsNotEmpty(String arg, String message)
    {
        if (arg == null || arg.isEmpty())
        {
            throw new NullPointerException(message);
        }

        return true;
    }
}
