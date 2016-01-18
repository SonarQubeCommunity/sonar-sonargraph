package com.hello2morrow.ddaexample.business.distributionpartner.service;

import java.util.Date;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.service.ObjectIdUtil;
import com.hello2morrow.dda.business.common.service.ServiceFactory;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
import com.hello2morrow.ddaexample.business.contact.service.AddressDto;
import com.hello2morrow.ddaexample.business.customer.service.CustomerControllerServiceIf;
import com.hello2morrow.ddaexample.business.customer.service.CustomerDto;
import com.hello2morrow.ddaexample.business.request.service.RequestControllerServiceIf;
import com.hello2morrow.ddaexample.business.request.service.RequestDto;
import com.hello2morrow.ddaexample.business.user.service.ContextDto;
import com.hello2morrow.ddaexample.business.user.service.LoginDto;
import com.hello2morrow.ddaexample.business.user.service.UserControllerServiceIf;

public class DistributionPartnerControllerServiceTest extends TestCase
{
    private static Logger s_Logger = Logger.getLogger(DistributionPartnerControllerServiceTest.class);

    private UserControllerServiceIf m_UserService;
    private DistributionPartnerControllerServiceIf m_DistributionPartnerService;
    private RequestControllerServiceIf m_RequestService;
    private CustomerControllerServiceIf m_CustomerService;

    protected void setUp() throws Exception
    {
        super.setUp();
        m_UserService = (UserControllerServiceIf) ServiceFactory.getInstance().getService(UserControllerServiceIf.class);
        m_DistributionPartnerService = (DistributionPartnerControllerServiceIf) ServiceFactory.getInstance().getService(
                DistributionPartnerControllerServiceIf.class);
        m_RequestService = (RequestControllerServiceIf) ServiceFactory.getInstance().getService(RequestControllerServiceIf.class);
        m_CustomerService = (CustomerControllerServiceIf) ServiceFactory.getInstance().getService(CustomerControllerServiceIf.class);
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        m_RequestService = null;
        m_DistributionPartnerService = null;
        m_UserService = null;
        m_CustomerService = null;
    }

    public void testCreateRequestForInformation()
    {
        LoginDto dto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        try
        {
            ContextDto ctx = m_UserService.login(dto);
            RequestForInformationDto requestDto = new RequestForInformationDto();
            requestDto.setSubject("rfi");
            requestDto.validate(DistributionPartnerControllerServiceIf.class,
                    DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_INFORMATION_CMD);
            s_Logger.info(m_DistributionPartnerService.createRequestForInformation(ctx, requestDto));

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

    public void testCreateRequestForOffer()
    {
        LoginDto dto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        try
        {
            ContextDto ctx = m_UserService.login(dto);
            RequestForOfferDto requestDto = new RequestForOfferDto();
            requestDto.setSubject("rfo");
            requestDto.validate(DistributionPartnerControllerServiceIf.class,
                    DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_INFORMATION_CMD);
            s_Logger.info(m_DistributionPartnerService.createRequestForOffer(ctx, requestDto));

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

    public void testCreateRequestForTestDrive()
    {
        s_Logger.info("#############################");
        s_Logger.info("testCreateRequestForTestDrive");
        s_Logger.info("#############################");
        LoginDto dto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        try
        {
            ContextDto ctx = m_UserService.login(dto);
            RequestForTestDriveDto requestDto = new RequestForTestDriveDto();
            requestDto.setSubject("rft");
            requestDto.setTargetDate(new Date());
            requestDto.validate(DistributionPartnerControllerServiceIf.class,
                    DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_TEST_DRIVE_CMD);
            s_Logger.info(m_DistributionPartnerService.createRequestForTestDrive(ctx, requestDto));

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

    public void testCreateAndAssignCustomersToSalesAssistant()
    {
        s_Logger.info("############################################");
        s_Logger.info("testCreateAndAssignCustomersToSalesAssistant");
        s_Logger.info("############################################");

        LoginDto dto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        try
        {
            ContextDto ctx = m_UserService.login(dto);

            s_Logger.info("-- create sales assistant");
            SalesAssistantDto salesAssistantDto = new SalesAssistantDto();
            salesAssistantDto.setFirstName("Dietmar");
            salesAssistantDto.setLastName("Menges");
            AddressDto addressDto = new AddressDto();
            addressDto.setCity("Dietramszell");
            addressDto.setZipCode("83623");
            addressDto.setStreet("Dietramszellerstrasse 13a");
            salesAssistantDto.validate(DistributionPartnerControllerServiceIf.class,
                    DistributionPartnerControllerServiceIf.CREATE_SALES_ASSISTANT_CMD);
            ObjectIdIf createdSalesAssitantId = m_DistributionPartnerService.createSalesAssistant(ctx, salesAssistantDto, addressDto);
            s_Logger.info("-- retrieve existing customers");
            CustomerDto[] customers = m_CustomerService.retrieveCustomers(ctx);
            s_Logger.info("-- found customers :");
            for (int i = 0; i < customers.length; i++)
            {
                s_Logger.info(customers[i]);
            }
            s_Logger.info("-- assign customers to sales assistant");
            m_DistributionPartnerService.assignCustomersToSalesAssistant(ctx, ObjectIdUtil.getIds(customers), createdSalesAssitantId);

            s_Logger.info("-- retrieve assigned customers for sales assistant : ");
            CustomerDto[] assignedCustomers = m_DistributionPartnerService.retrieveAssignedCustomersForSalesAssistant(ctx,
                            createdSalesAssitantId);
            for (int i = 0; i < assignedCustomers.length; i++)
            {
                s_Logger.info(assignedCustomers[i]);
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