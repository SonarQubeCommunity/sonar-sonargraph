package com.hello2morrow.ddaexample.business.request.service;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.service.ServiceFactory;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
import com.hello2morrow.ddaexample.business.user.service.ContextDto;
import com.hello2morrow.ddaexample.business.user.service.LoginDto;
import com.hello2morrow.ddaexample.business.user.service.UserControllerServiceIf;

public class RequestControllerServiceTest extends TestCase
{
    private static Logger s_Logger = Logger.getLogger(RequestControllerServiceTest.class);

    private UserControllerServiceIf m_UserService;
    private RequestControllerServiceIf m_RequestService;

    protected void setUp() throws Exception
    {
        super.setUp();
        m_UserService = (UserControllerServiceIf) ServiceFactory.getInstance().getService(UserControllerServiceIf.class);
        m_RequestService = (RequestControllerServiceIf) ServiceFactory.getInstance().getService(
                        RequestControllerServiceIf.class);
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        m_RequestService = null;
        m_UserService = null;
    }

    public void testRetrieveRequests()
    {
        LoginDto dto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        try
        {
            ContextDto ctx = m_UserService.login(dto);
            RequestDto[] dtos = m_RequestService.retrieveRequests(ctx);
            for (int i = 0; i < dtos.length; i++)
            {
                s_Logger.info(dtos[i]);
            }
        }
        catch (BusinessException e)
        {
            e.printStackTrace();
        }
        catch (TechnicalException e)
        {
            e.printStackTrace();
        }
    }
}