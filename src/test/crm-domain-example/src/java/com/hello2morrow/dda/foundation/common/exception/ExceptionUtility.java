package com.hello2morrow.dda.foundation.common.exception;

import java.io.PrintStream;

public final class ExceptionUtility
{
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    private ExceptionUtility()
    {
        //Just to make it unaccessible
    }

    public static String collectAll(Throwable exc)
    {
        StringBuffer buffer = new StringBuffer();
        assert exc != null;
        Throwable next = exc;
        while (next != null)
        {
            buffer.append(next.toString());
            buffer.append(LINE_SEPARATOR);
            StackTraceElement[] ste = next.getStackTrace();
            for (int i = 0; i < ste.length; i++)
            {
                buffer.append(ste[i].toString());
                buffer.append(LINE_SEPARATOR);
            }
            next = next.getCause();
        }

        return buffer.toString();
    }

    public static void printAll(PrintStream out, Throwable exc)
    {
        assert out != null;
        assert exc != null;

        for (Throwable next = exc; next != null; next = next.getCause())
        {
            out.println(next.getMessage());
            next.printStackTrace(out);
        }
    }
}