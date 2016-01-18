package com.hello2morrow.dda.foundation.common.exception;

public class TechnicalException extends RuntimeException
{
    public TechnicalException(String msg)
    {
        super(msg);
    }

    public TechnicalException(Throwable cause)
    {
        super(cause);
    }

    public TechnicalException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}