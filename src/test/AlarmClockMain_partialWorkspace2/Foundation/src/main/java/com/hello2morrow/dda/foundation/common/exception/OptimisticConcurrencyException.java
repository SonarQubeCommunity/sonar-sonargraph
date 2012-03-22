package com.hello2morrow.dda.foundation.common.exception;


public final class OptimisticConcurrencyException extends TechnicalException
{
    public OptimisticConcurrencyException(String msg)
    {
        super(msg);
    }

    public OptimisticConcurrencyException(Throwable cause)
    {
        super(cause);
    }

    public OptimisticConcurrencyException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
