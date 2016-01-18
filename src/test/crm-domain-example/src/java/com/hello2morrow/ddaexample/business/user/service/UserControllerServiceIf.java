/**
 * 23.05.2005 17:28:20 - generated 'service-interface' for 'com.hello2morrow.ddaexample.business.user.controller.UserController'
 */

package com.hello2morrow.ddaexample.business.user.service;

import com.hello2morrow.dda.business.common.service.ServiceIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public interface UserControllerServiceIf extends ServiceIf
{
	public final static String LOGIN_CMD = "User::LoginCmd";
	public com.hello2morrow.ddaexample.business.user.service.ContextDto login(com.hello2morrow.ddaexample.business.user.service.LoginDto loginDto) 
		throws BusinessException, TechnicalException;

	public final static String CREATE_USER_CMD = "User::CreateUserCmd";
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createUser(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.user.service.UserDto userDto) 
		throws BusinessException, TechnicalException;

	public final static String ADD_ROLE_TO_USER_CMD = "User::AddRoleToUserCmd";
	public void addRoleToUser(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.dda.foundation.common.ObjectIdIf userId, com.hello2morrow.dda.foundation.common.ObjectIdIf roleId) 
		throws BusinessException, TechnicalException;

	public final static String DELETE_USER_CMD = "User::DeleteUserCmd";
	public void deleteUser(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.dda.foundation.common.ObjectIdIf userId) 
		throws BusinessException, TechnicalException;

	public final static String RETRIEVE_USERS_CMD = "User::RetrieveUsersCmd";
	public com.hello2morrow.ddaexample.business.user.service.UserListDto retrieveUsers(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto) 
		throws BusinessException, TechnicalException;

	public final static String RETRIEVE_ROLES_CMD = "User::RetrieveRolesCmd";
	public com.hello2morrow.ddaexample.business.user.service.RoleDto[] retrieveRoles(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto) 
		throws BusinessException, TechnicalException;

	public final static String RESET_USER_PASSWORD_CMD = "User::ResetUserPasswordCmd";
	public void resetUserPwd(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.dda.foundation.common.ObjectIdIf userId) 
		throws BusinessException, TechnicalException;

	public final static String CHANGE_USER_PASSWORD_CMD = "User::ChangeUserPasswordCmd";
	public void changeUserPwd(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.user.service.ChangePasswordDto changePasswordDto) 
		throws BusinessException, TechnicalException;

}