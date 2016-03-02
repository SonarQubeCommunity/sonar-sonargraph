package com.hello2morrow.dda.business.common.service;

import java.util.ArrayList;
import java.util.List;

public abstract class DtoValidator implements DtoValidatorIf
{
    private List m_Errors = new ArrayList();

    protected final void addError(String error)
    {
        assert error != null;
        assert error.length() > 0;
        assert !m_Errors.contains(error);
        m_Errors.add(error);
    }

    protected final boolean hasErrors()
    {
        return m_Errors.size() > 0;
    }

    public final String[] getErrors()
    {
        assert hasErrors();
        return (String[]) m_Errors.toArray(new String[0]);
    }

    public final void clearErrors()
    {
        m_Errors.clear();
    }
}