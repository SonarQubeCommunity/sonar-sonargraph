package com.hello2morrow.dda.business.common.service;

import com.hello2morrow.dda.foundation.common.exception.DtoValidationException;

interface DtoValidatorIf
{
    public void clearErrors();

    public String[] getErrors();

    public void validate(String serverCmdId, DtoIf dto) throws DtoValidationException;
}