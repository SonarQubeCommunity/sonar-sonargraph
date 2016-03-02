/**
 * 23.05.2005 17:28:20 - generated 'service-adapter' for 'com.hello2morrow.ddaexample.business.user.controller.UserController'
 */

package com.hello2morrow.ddaexample.business.user.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.dsi.DomainObjectFactory;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class UserControllerServiceAdapter implements com.hello2morrow.ddaexample.business.user.service.UserControllerServiceIf
{
    private final static Logger s_Logger = Logger.getLogger(UserControllerServiceAdapter.class);
    private final UserController m_Controller;

    public UserControllerServiceAdapter()
    {
        m_Controller = new UserController();
    }

	public com.hello2morrow.ddaexample.business.user.service.ContextDto login(com.hello2morrow.ddaexample.business.user.service.LoginDto loginDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.ddaexample.business.user.service.ContextDto result = m_Controller.login(loginDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createUser(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.user.service.UserDto userDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.dda.foundation.common.ObjectIdIf result = m_Controller.createUser(contextDto, userDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
	public void addRoleToUser(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.dda.foundation.common.ObjectIdIf userId, com.hello2morrow.dda.foundation.common.ObjectIdIf roleId) throws BusinessException, TechnicalException
	{
		m_Controller.addRoleToUser(contextDto, userId, roleId);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
	}
	
	public void deleteUser(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.dda.foundation.common.ObjectIdIf userId) throws BusinessException, TechnicalException
	{
		m_Controller.deleteUser(contextDto, userId);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
	}
	
	public com.hello2morrow.ddaexample.business.user.service.UserListDto retrieveUsers(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.ddaexample.business.user.service.UserListDto result = m_Controller.retrieveUsers(contextDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
	public com.hello2morrow.ddaexample.business.user.service.RoleDto[] retrieveRoles(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.ddaexample.business.user.service.RoleDto[] result = m_Controller.retrieveRoles(contextDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
	public void resetUserPwd(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.dda.foundation.common.ObjectIdIf userId) throws BusinessException, TechnicalException
	{
		m_Controller.resetUserPwd(contextDto, userId);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
	}
	
	public void changeUserPwd(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.user.service.ChangePasswordDto changePasswordDto) throws BusinessException, TechnicalException
	{
		m_Controller.changeUserPwd(contextDto, changePasswordDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
	}
	
}