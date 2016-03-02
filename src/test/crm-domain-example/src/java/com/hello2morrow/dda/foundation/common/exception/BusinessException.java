package com.hello2morrow.dda.foundation.common.exception;

public class BusinessException extends RuntimeException
{
    public BusinessException(String msg)
    {
        super(msg);
    }

    public BusinessException(Throwable cause)
    {
        super(cause);
    }

    public BusinessException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}