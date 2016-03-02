package com.hello2morrow.ddaexample.business.testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DomainObjectFactory;
import com.hello2morrow.dda.business.common.service.Dto;
import com.hello2morrow.dda.business.common.service.ServiceFactory;
import com.hello2morrow.dda.integration.common.esi.EsiFactory;
import com.hello2morrow.ddaexample.business.customer.service.CustomerControllerServiceIf;
import com.hello2morrow.ddaexample.business.customer.service.CustomerControllerServiceTest;
import com.hello2morrow.ddaexample.business.distributionpartner.service.DistributionPartnerControllerServiceIf;
import com.hello2morrow.ddaexample.business.distributionpartner.service.DistributionPartnerControllerServiceTest;
import com.hello2morrow.ddaexample.business.request.service.RequestControllerServiceIf;
import com.hello2morrow.ddaexample.business.request.service.RequestControllerServiceTest;
import com.hello2morrow.ddaexample.business.user.domain.Role;
import com.hello2morrow.ddaexample.business.user.domain.ServerCommand;
import com.hello2morrow.ddaexample.business.user.domain.User;
import com.hello2morrow.ddaexample.business.user.service.UserControllerServiceIf;
import com.hello2morrow.ddaexample.business.user.service.UserControllerServiceTest;

public class BusinessTestSuiteWithoutAppServer extends TestSuite
{
    private static Logger s_Logger = Logger.getLogger(BusinessTestSuiteWithoutAppServer.class);

    public static Test suite() throws Exception
    {
        TestSuite suite = new BusinessTestSuiteWithoutAppServer();
        return suite;
    }

    public BusinessTestSuiteWithoutAppServer() throws Exception
    {
        super("Business Test Suite Without App Server");

        DtoManager.createInstance();
        Dto.initialize("/dto-validation.properties");
        DomainObjectFactory.createInstance("/domain-object-factory.properties");
        ServiceFactory.createInstance("/service-factory-test.properties");
        EsiFactory.createInstance("/esi-factory-test.properties");
        DataManagerFactory.createInstance("/data-manager-factory-test.properties");

        setupPersistentData();

        addTestSuite(UserControllerServiceTest.class);
        addTestSuite(RequestControllerServiceTest.class);
        addTestSuite(CustomerControllerServiceTest.class);
        addTestSuite(DistributionPartnerControllerServiceTest.class);
    }

    private void setupPersistentData() throws Exception
    {
        ServerCommand scCreateUser = new ServerCommand(UserControllerServiceIf.CREATE_USER_CMD);
        ServerCommand scDeleteUser = new ServerCommand(UserControllerServiceIf.DELETE_USER_CMD);
        ServerCommand scRetrieveUsers = new ServerCommand(UserControllerServiceIf.RETRIEVE_USERS_CMD);
        ServerCommand scRetrieveRoles = new ServerCommand(UserControllerServiceIf.RETRIEVE_ROLES_CMD);
        ServerCommand scChangeUserPwd = new ServerCommand(UserControllerServiceIf.CHANGE_USER_PASSWORD_CMD);
        ServerCommand scResetUserPwd = new ServerCommand(UserControllerServiceIf.RESET_USER_PASSWORD_CMD);
        ServerCommand scCreateRequestForInformation = new ServerCommand(
                        DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_INFORMATION_CMD);
        ServerCommand scCreateRequestForOffer = new ServerCommand(
                        DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_OFFER_CMD);
        ServerCommand scCreateRequestForTestDrive = new ServerCommand(
                        DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_TEST_DRIVE_CMD);
        ServerCommand scCreateSalesAssistant = new ServerCommand(
                        DistributionPartnerControllerServiceIf.CREATE_SALES_ASSISTANT_CMD);
        ServerCommand scGetRequests = new ServerCommand(RequestControllerServiceIf.RETRIEVE_REQUESTS_CMD);

        ServerCommand scCreateCustomer = new ServerCommand(CustomerControllerServiceIf.CREATE_CUSTOMER_CMD);
        ServerCommand scCreateVipCustomer = new ServerCommand(CustomerControllerServiceIf.CREATE_VIPCUSTOMER_CMD);
        ServerCommand scGetCustomers = new ServerCommand(CustomerControllerServiceIf.RETRIEVE_CUSTOMERS_CMD);
        ServerCommand scAssignCustomerToSalesAssistant = new ServerCommand(
                        DistributionPartnerControllerServiceIf.ASSIGN_CUSTOMERS_TO_SALES_ASSISTANT_CMD);
        ServerCommand scRetrieveAssignedCustomerForSalesAssistant = new ServerCommand(
                        DistributionPartnerControllerServiceIf.RETRIEVE_ASSIGNED_CUSTOMERS_FOR_SALES_ASSISTANT_CMD);
        ServerCommand scAddRoleToUserCmd = new ServerCommand(UserControllerServiceIf.ADD_ROLE_TO_USER_CMD);

        Role adminRole = new Role("Administrator");

        adminRole.setServerCommands(new ServerCommand[] { scAddRoleToUserCmd, scCreateUser, scDeleteUser,
                        scRetrieveUsers, scRetrieveRoles, scResetUserPwd, scChangeUserPwd, scCreateRequestForTestDrive,
                        scCreateRequestForInformation, scCreateRequestForOffer, scCreateCustomer, scGetRequests,
                        scCreateSalesAssistant, scCreateVipCustomer, scGetCustomers, scAssignCustomerToSalesAssistant,
                        scRetrieveAssignedCustomerForSalesAssistant});

        User admin = new User("Administrator", "HtojdYvp425eDSpqh95YSqygGT8=");
        admin.setRoles(new Role[] { adminRole});

        Role guestRole = new Role("Guest");
    }
}