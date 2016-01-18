package com.hello2morrow.ddaexample.business.user.service;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.service.ServiceFactory;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class UserControllerServiceTest extends TestCase
{
    private static Logger s_Logger = Logger.getLogger(UserControllerServiceTest.class);
    private UserControllerServiceIf m_UserService;

    protected void setUp() throws Exception
    {
        super.setUp();
        m_UserService = (UserControllerServiceIf) ServiceFactory.getInstance().getService(UserControllerServiceIf.class);
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        m_UserService = null;
    }

    public void testLogin()
    {
        s_Logger.info("--> testLogin");
        LoginDto dto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        try
        {
            s_Logger.info("--> trying to log in with = ");
            s_Logger.info(dto);
            ContextDto ctxDto = m_UserService.login(dto);
            s_Logger.info("--> successfull - context = ");
            s_Logger.info(dto);
        }
        catch (BusinessException e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
        catch (TechnicalException e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testCreateUser()
    {
        s_Logger.info("--> testCreateUser");
        LoginDto loginDto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");

        try
        {
            ContextDto ctxDto = m_UserService.login(loginDto);
            RoleDto[] roleDtos = m_UserService.retrieveRoles(ctxDto);

            ObjectIdIf guestRoleId = null;
            ObjectIdIf adminRoleId = null;

            for (int i = 0; i < roleDtos.length; i++)
            {
                s_Logger.info("--> found role in system = ");
                s_Logger.info(roleDtos[i]);
                if (roleDtos[i].getName().equals("Guest"))
                {
                    guestRoleId = roleDtos[i].getObjectId();
                }
                else if (roleDtos[i].getName().equals("Administrator"))
                {
                    adminRoleId = roleDtos[i].getObjectId();
                }
            }

            assert guestRoleId != null;
            assert adminRoleId != null;

            UserDto userDto = new UserDto();
            userDto.setName("butch");
            userDto.setPassword("butch");
            userDto.addRolesReference(guestRoleId);

            s_Logger.info("--> trying to create user");
            ObjectIdIf createdUserId = m_UserService.createUser(ctxDto, userDto);
            s_Logger.info("--> user created = ");
            s_Logger.info(createdUserId);
            s_Logger.info("--> trying to add another role to user");
            m_UserService.addRoleToUser(ctxDto, createdUserId, adminRoleId);
            s_Logger.info("--> trying to get system user list");
            UserListDto userListDto = m_UserService.retrieveUsers(ctxDto);
            s_Logger.info("--> system user list = ");
            s_Logger.info(userListDto);
            s_Logger.info("--> trying to delete user");
            m_UserService.deleteUser(ctxDto, createdUserId);
            s_Logger.info("--> trying to get system user list");
            userListDto = m_UserService.retrieveUsers(ctxDto);
            s_Logger.info("--> system user list = ");
            s_Logger.info(userListDto);
        }
        catch (BusinessException e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
        catch (TechnicalException e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testRetrieveUsers()
    {
        LoginDto dto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        try
        {
            ContextDto ctx = m_UserService.login(dto);
            UserListDto userListDto = m_UserService.retrieveUsers(ctx);
            s_Logger.info(userListDto);
        }
        catch (BusinessException e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
        catch (TechnicalException e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testRetrieveRoles()
    {
        LoginDto dto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        try
        {
            ContextDto ctx = m_UserService.login(dto);
            RoleDto[] roleDtos = m_UserService.retrieveRoles(ctx);

            s_Logger.info("roles : ");
            for (int i = 0; i < roleDtos.length; i++)
            {
                s_Logger.info(roleDtos[i].getName());
            }
        }
        catch (BusinessException e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
        catch (TechnicalException e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testResetUserPwd()
    {
        //TODO
    }

    public void testChangeUserPwd()
    {
        //TODO
    }
}