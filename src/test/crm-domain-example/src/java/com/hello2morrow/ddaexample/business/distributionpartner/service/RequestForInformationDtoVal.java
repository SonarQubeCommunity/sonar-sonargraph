package com.hello2morrow.ddaexample.business.distributionpartner.service;

import com.hello2morrow.dda.business.common.service.DtoIf;
import com.hello2morrow.dda.business.common.service.DtoValidator;
import com.hello2morrow.dda.foundation.common.exception.DtoValidationException;

public final class RequestForInformationDtoVal extends DtoValidator
{
    public void validate(String serverCmdId, DtoIf dto) throws DtoValidationException
    {
        assert dto != null;
        assert dto instanceof RequestForInformationDto;
        assert !hasErrors();
        RequestForInformationDto requestDto = (RequestForInformationDto) dto;
        String subject = requestDto.getSubject();
        if (subject == null || subject.length() == 0)
        {
            addError("no subject specified");
        }
        if (hasErrors())
        {
            throw new DtoValidationException(getErrors());
        }
    }
}