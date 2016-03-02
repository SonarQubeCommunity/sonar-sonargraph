package com.hello2morrow.ddaexample.business.user.service;

import com.hello2morrow.dda.business.common.service.DtoIf;
import com.hello2morrow.dda.business.common.service.DtoValidator;
import com.hello2morrow.dda.foundation.common.exception.DtoValidationException;

public final class ChangePasswordDtoVal extends DtoValidator
{
    public void validate(String serverCmdId, DtoIf baseDto) throws DtoValidationException
    {
        assert baseDto != null;
        assert baseDto instanceof ChangePasswordDto;
        assert !hasErrors();
        ChangePasswordDto dto = (ChangePasswordDto) baseDto;
        String currentPwd = dto.getCurrentPwd();
        String currentEncryptedPwd = dto.getCurrentEncryptedPwd();
        validate(currentPwd, currentEncryptedPwd, "Aktuelles");

        String newPwd = dto.getNewPwd();
        String newEncryptedPwd = dto.getNewEncryptedPwd();
        validate(newPwd, newEncryptedPwd, "Neues");

        String newPwdRepeated = dto.getNewPwdRepeated();
        String newEncryptedPwdRepeated = dto.getNewEncryptedPwdRetyped();
        validate(newPwdRepeated, newEncryptedPwdRepeated, "Neues (wiederholtes)");

        if (newPwd != null)
        {
            presentationTierCheck(currentPwd, newPwd, newPwdRepeated);
        }
        if (newPwd == null)
        {
            businessTierCheck(currentEncryptedPwd, newEncryptedPwd, newEncryptedPwdRepeated);
        }
        if (hasErrors())
        {
            throw new DtoValidationException(getErrors());
        }
    }

    private void businessTierCheck(String currentEncryptedPwd, String newEncryptedPwd, String newEncryptedPwdRepeated)
    {
        if (newEncryptedPwd != null)
        {
            assert newEncryptedPwdRepeated != null;
            assert currentEncryptedPwd != null;
            if (newEncryptedPwd.equals(currentEncryptedPwd))
            {
                addError("Das neue verschlüsselte Passwort ist identisch mit dem alten verschlüsselten Passwort");
            }
            else if (!newEncryptedPwd.equals(newEncryptedPwdRepeated))
            {
                addError("Das neue verschlüsselte Passwort ist nicht identisch mit dem verschlüsselten wiederholten neuen Passwort");
            }
        }
        else
        {
            addError("Verschlüsselte Passworte sind ungültig");
        }
    }

    private void presentationTierCheck(String currentPwd, String newPwd, String newPwdRepeated)
    {
        assert newPwd != null;
        assert currentPwd != null;
        assert newPwdRepeated != null;

        if (currentPwd.equals(newPwd))
        {
            addError("Das neue Passwort ist identisch mit dem alten Passwort");
        }
        else if (!newPwd.equals(newPwdRepeated))
        {
            addError("Das neue Passwort ist nicht identisch mit dem wiederholten neuen Passwort");
        }
    }

    private void validate(String pwd, String encryptedPwd, String pwdType)
    {
        assert pwdType != null;
        assert pwdType.length() > 0;

        if (pwd != null && pwd.length() < 3) //presentation tier
        {
            addError(pwdType + " Passwort ungültig - mindestens 3 Zeichen sind notwendig");
        }
        if (pwd == null && encryptedPwd == null) //business tier
        {
            addError(pwdType + " Verschlüsseltes Passwort ungültig");
        }

    }
}