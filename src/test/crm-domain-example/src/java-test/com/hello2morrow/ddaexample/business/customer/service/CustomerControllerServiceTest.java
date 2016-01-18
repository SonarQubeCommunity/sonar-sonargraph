package com.hello2morrow.ddaexample.business.customer.service;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.service.ServiceFactory;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
import com.hello2morrow.ddaexample.business.contact.service.AddressDto;
import com.hello2morrow.ddaexample.business.user.service.ContextDto;
import com.hello2morrow.ddaexample.business.user.service.LoginDto;
import com.hello2morrow.ddaexample.business.user.service.UserControllerServiceIf;

public class CustomerControllerServiceTest extends TestCase
{
    private static Logger s_Logger = Logger.getLogger(CustomerControllerServiceTest.class);
    private CustomerControllerServiceIf m_CustomerControllerService;
    private UserControllerServiceIf m_UserControllerService;

    protected void setUp() throws Exception
    {
        super.setUp();
        m_UserControllerService = (UserControllerServiceIf) ServiceFactory.getInstance().getService(UserControllerServiceIf.class);
        m_CustomerControllerService = (CustomerControllerServiceIf) ServiceFactory.getInstance().getService(CustomerControllerServiceIf.class);
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        m_UserControllerService = null;
        m_CustomerControllerService = null;
    }

    public void testCreateCustomer()
    {
        LoginDto dto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        try
        {
            ContextDto contextDto = m_UserControllerService.login(dto);

            CustomerDto customerDto = new CustomerDto();
            customerDto.setFirstName("Juliana");
            customerDto.setLastName("Velez");
            customerDto.setAge(33);

            AddressDto addressDto = new AddressDto();
            addressDto.setCity("Dietramszell");
            addressDto.setZipCode("83623");
            addressDto.setStreet("Dietramszellerstrasse 13a");

            m_CustomerControllerService.createCustomer(contextDto, customerDto, addressDto);
            CustomerDto[] customers = m_CustomerControllerService.retrieveCustomers(contextDto);
            s_Logger.info("--> customer list = ");
            for (int i = 0; i < customers.length; i++)
            {
                s_Logger.info(customers[i]);
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

    public void testCreateCustomersAndVipCustomers()
    {
        LoginDto dto = new LoginDto("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        try
        {
            ContextDto contextDto = m_UserControllerService.login(dto);

            for (int i = 0; i < 10; i++)
            {
                CustomerDto customerDto = new CustomerDto();
                customerDto.setFirstName("First_Customer_" + i);
                customerDto.setLastName("Last_Customer_" + i);
                customerDto.setAge(30 + i);

                AddressDto addressDto = new AddressDto();
                addressDto.setCity("Dietramszell");
                addressDto.setZipCode("83623");
                addressDto.setStreet("Dietramszellerstrasse" + (13 + i));

                m_CustomerControllerService.createCustomer(contextDto, customerDto, addressDto);
            }

            for (int i = 0; i < 5; i++)
            {
                VipCustomerDto customerDto = new VipCustomerDto();
                customerDto.setFirstName("First_Vip_Customer_" + i);
                customerDto.setLastName("Last_Vip_Customer_" + i);
                customerDto.setAge(30 + i);

                AddressDto addressDto = new AddressDto();
                addressDto.setCity("Dietramszell");
                addressDto.setZipCode("83623");
                addressDto.setStreet("Dietramszellervipstrasse" + (13 + i));

                m_CustomerControllerService.createVipCustomer(contextDto, customerDto, addressDto);
            }

            CustomerDto[] customers = m_CustomerControllerService.retrieveCustomers(contextDto);
            s_Logger.info("--> customer list = ");
            for (int i = 0; i < customers.length; i++)
            {
                s_Logger.info(customers[i]);
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
}