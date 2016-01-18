package com.hello2morrow.ddaexample.business.user.service;

import com.hello2morrow.dda.business.common.service.DtoIf;
import com.hello2morrow.dda.business.common.service.DtoValidator;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.DtoValidationException;

public final class UserDtoVal extends DtoValidator
{
    public void validate(String serverCmdId, DtoIf dto) throws DtoValidationException
    {
        assert dto != null;
        assert dto instanceof UserDto;
        assert !hasErrors();
        assert serverCmdId != null;

        if (UserControllerServiceIf.CREATE_USER_CMD.equals(serverCmdId))
        {
            UserDto userDto = (UserDto) dto;
            String userName = userDto.getName();
            ObjectIdIf[] rolRefs = userDto.getRolesReferences();
            if (userName.length() < 3)
            {
                addError("name of user needs at least 3 characters");
            }
            if (rolRefs.length < 1)
            {
                addError("user needs at least one role");
            }

            if (hasErrors())
            {
                throw new DtoValidationException(getErrors());
            }
        }
    }
}