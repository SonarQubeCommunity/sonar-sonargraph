package com.hello2morrow.ddaexample.business.user.service;

import com.hello2morrow.dda.business.common.service.DtoIf;
import com.hello2morrow.dda.business.common.service.DtoValidator;
import com.hello2morrow.dda.foundation.common.exception.DtoValidationException;

public final class LoginDtoVal extends DtoValidator
{
    public void validate(String serverCmdId, DtoIf dto) throws DtoValidationException
    {
        assert dto != null;
        assert dto instanceof LoginDto;
        assert !hasErrors();
        LoginDto loginDto = (LoginDto) dto;
        String userName = loginDto.getUserName();
        String encryptedPwd = loginDto.getEncryptedPwd();
        if (userName.length() < 3)
        {
            addError("Name des Benutzers ungültig - mindestens 3 Zeichen sind notwendig");
        }
        if (encryptedPwd == null) //business tier
        {
            addError("Verschlüsseltes Passwort ungültig");
        }

        if (hasErrors())
        {
            throw new DtoValidationException(getErrors());
        }
    }
}