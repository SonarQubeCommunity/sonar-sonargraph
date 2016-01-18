package com.hello2morrow.dda.foundation.common.exception;

import java.util.HashSet;
import java.util.Set;

final public class AssertionUtility
{
    private AssertionUtility()
    {
        //Only to make the ctor unaccessible
    }

    public static boolean checkArray(Object[] array)
    {
        if (array == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < array.length; ++i)
            {
                if (array[i] == null)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkUniqueElementsArray(Object[] array)
    {
        if (array == null)
        {
            return false;
        }
        else
        {
            Set elements = new HashSet();
            for (int i = 0; i < array.length; ++i)
            {
                if (array[i] == null)
                {
                    return false;
                }
                else if (!elements.add(array[i]))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkArray(Object[][] array)
    {
        if (array == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < array.length; ++i)
            {
                Object[] next = array[i];
                if (next == null)
                {
                    return false;
                }
                for (int j = 0; j < next.length; ++j)
                {
                    if (array[i][j] == null)
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}