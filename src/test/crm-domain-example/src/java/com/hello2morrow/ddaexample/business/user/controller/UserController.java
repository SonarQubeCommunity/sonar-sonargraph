package com.hello2morrow.ddaexample.business.user.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
import com.hello2morrow.ddaexample.business.user.domain.Role;
import com.hello2morrow.ddaexample.business.user.domain.ServerCommand;
import com.hello2morrow.ddaexample.business.user.domain.User;
import com.hello2morrow.ddaexample.business.user.service.ChangePasswordDto;
import com.hello2morrow.ddaexample.business.user.service.ContextDto;
import com.hello2morrow.ddaexample.business.user.service.LoginDto;
import com.hello2morrow.ddaexample.business.user.service.RoleDto;
import com.hello2morrow.ddaexample.business.user.service.UserControllerServiceIf;
import com.hello2morrow.ddaexample.business.user.service.UserDto;
import com.hello2morrow.ddaexample.business.user.service.UserListDto;

/** 
 * @dda-generate-service
 */
public final class UserController implements UserControllerServiceIf
{
    private static Logger s_Logger = Logger.getLogger(UserController.class);

    /**
     * @dda-service LOGIN_CMD = "User::LoginCmd"
     */
    public ContextDto login(LoginDto loginDto) throws BusinessException, TechnicalException
    {
        assert loginDto != null;
        loginDto.validate(UserControllerServiceIf.class, UserControllerServiceIf.LOGIN_CMD);

        User user = User.findUserByName(loginDto.getUserName());
        if (user == null)
        {
            throw new BusinessException("user '" + loginDto.getUserName() + "' unknown");
        }

        assert user != null;
        user.login(loginDto.getEncryptedPwd());

        Role[] roles = user.getRoles();
        ContextDto contextVo = new ContextDto(user.getObjectId(), loginDto.getUserName());
        for (int i = 0; i < roles.length; i++)
        {
            Role nextRole = roles[i];
            ObjectIdIf roleReference = nextRole.getObjectId();
            contextVo.addRole(nextRole.getName(), roleReference);
        }

        return contextVo;
    }

    /**
     * @dda-service CREATE_USER_CMD = "User::CreateUserCmd"
     */
    public ObjectIdIf createUser(ContextDto contextDto, UserDto userDto) throws BusinessException, TechnicalException
    {
        assert userDto != null;
        checkPermission(contextDto, UserControllerServiceIf.CREATE_USER_CMD);
        userDto.validate(UserControllerServiceIf.class, UserControllerServiceIf.CREATE_USER_CMD);

        User found = User.findUserByName(userDto.getName());
        if (found != null)
        {
            throw new BusinessException("user '" + userDto.getName() + "' already exists");
        }
        else
        {
            User user = new User();
            UserDtoMapper.mapDtoToDomainObject(userDto, user, true);
            return user.getObjectId();
        }
    }

    /**
     * @dda-service ADD_ROLE_TO_USER_CMD = "User::AddRoleToUserCmd"
     */
    public void addRoleToUser(ContextDto contextDto, ObjectIdIf userId, ObjectIdIf roleId) throws BusinessException,
                    TechnicalException
    {
        assert userId != null;
        assert roleId != null;
        checkPermission(contextDto, UserControllerServiceIf.ADD_ROLE_TO_USER_CMD);

        User user = (User) DomainObjectWithDataSupplier.findByObjectId(userId);
        assert user != null;
        Role role = (Role) DomainObjectWithDataSupplier.findByObjectId(roleId);
        assert role != null;
        user.addRole(role);
    }

    /**
     * @dda-service DELETE_USER_CMD = "User::DeleteUserCmd"
     */
    public void deleteUser(ContextDto contextDto, ObjectIdIf userId) throws BusinessException, TechnicalException
    {
        assert userId != null;
        checkPermission(contextDto, UserControllerServiceIf.DELETE_USER_CMD);

        User found = (User) DomainObjectWithDataSupplier.findByObjectId(userId);
        if (found == null)
        {
            throw new BusinessException("user with id '" + userId + "' not found");
        }
        else
        {
            found.delete();
        }
    }

    public static void checkPermission(ContextDto contextDto, String serverCommandId) throws TechnicalException,
                    BusinessException
    {
        assert contextDto != null;
        ObjectIdIf executingUserId = contextDto.getUserReference();
        User executingUser = (User) DomainObjectWithDataSupplier.findByObjectId(executingUserId);
        ServerCommand serverCommand = ServerCommand.findServerCommandByName(serverCommandId);
        if (serverCommand == null)
        {
            throw new BusinessException("server command '" + serverCommandId + "' does not exist");
        }
        if (!executingUser.hasPermissionToExecute(serverCommand))
        {
            throw new BusinessException("user '" + executingUser.getName() + "' has no permission '" + serverCommandId
                            + "' to execute");
        }
    }

    /**
     * @dda-service RETRIEVE_USERS_CMD = "User::RetrieveUsersCmd"
     */
    public UserListDto retrieveUsers(ContextDto contextDto) throws BusinessException, TechnicalException
    {
        checkPermission(contextDto, UserControllerServiceIf.RETRIEVE_USERS_CMD);
        UserListDto userListDto = new UserListDto();

        User[] users = User.findAllUsers();
        for (int i = 0; i < users.length; i++)
        {
            User nextUser = users[i];
            Role[] nextRoles = nextUser.getRoles();
            String[] nextRolesAsStrings = new String[nextRoles.length];
            ObjectIdIf[] nextRoleObjectIds = new ObjectIdIf[nextRoles.length];
            for (int j = 0; j < nextRoles.length; j++)
            {
                nextRolesAsStrings[j] = nextRoles[j].getName();
                nextRoleObjectIds[j] = nextRoles[j].getObjectId();
            }
            userListDto.addUser(nextUser.getName(), nextUser.getObjectId(), nextRolesAsStrings, nextRoleObjectIds);
        }

        return userListDto;
    }

    /**
     * @dda-service RETRIEVE_ROLES_CMD = "User::RetrieveRolesCmd"
     */
    public RoleDto[] retrieveRoles(ContextDto contextDto) throws BusinessException, TechnicalException
    {
        checkPermission(contextDto, UserControllerServiceIf.RETRIEVE_ROLES_CMD);
        Role[] roles = Role.findAllRoles();
        return RoleDtoMapper.createDtosFromDomainObjects(roles);
    }

    /**
     * @dda-service RESET_USER_PASSWORD_CMD = "User::ResetUserPasswordCmd"
     */
    public void resetUserPwd(ContextDto contextDto, ObjectIdIf userId) throws BusinessException, TechnicalException
    {
        checkPermission(contextDto, UserControllerServiceIf.RESET_USER_PASSWORD_CMD);
        assert userId != null;
        User user = (User) DomainObjectWithDataSupplier.findByObjectId(userId);
        if (user == null)
        {
            throw new BusinessException("user with id '" + userId + " does not exist");
        }
        else
        {
            user.setPassword(user.getName());
        }
    }

    /**
     * @dda-service CHANGE_USER_PASSWORD_CMD = "User::ChangeUserPasswordCmd"
     */
    public void changeUserPwd(ContextDto contextDto, ChangePasswordDto changePasswordDto) throws BusinessException,
                    TechnicalException
    {
        checkPermission(contextDto, UserControllerServiceIf.CHANGE_USER_PASSWORD_CMD);
        assert changePasswordDto != null;
        changePasswordDto.validate(UserControllerServiceIf.class, UserControllerServiceIf.CHANGE_USER_PASSWORD_CMD);
    }
}