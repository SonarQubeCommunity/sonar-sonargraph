package com.hello2morrow.dda.foundation.common.exception;



public final class DtoValidationException extends BusinessException
{
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");
    private final String[] m_Errors;

    private static String createMessage(String[] errors)
    {
        assert AssertionUtility.checkArray(errors);
        StringBuffer buffer = new StringBuffer("dto validation errors : ");
        for (int i = 0; i < errors.length; i++)
        {
            buffer.append(LINE_SEPARATOR);
            buffer.append(errors[i]);
        }
        return buffer.toString();
    }

    public DtoValidationException(String[] errors)
    {
        super(createMessage(errors));
        assert AssertionUtility.checkArray(errors);
        m_Errors = errors;
    }

    public String[] getValidationErrors()
    {
        return m_Errors;
    }
}