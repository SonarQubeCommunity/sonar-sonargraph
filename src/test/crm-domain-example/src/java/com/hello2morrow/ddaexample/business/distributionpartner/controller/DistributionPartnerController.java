package com.hello2morrow.ddaexample.business.distributionpartner.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
import com.hello2morrow.ddaexample.business.contact.controller.AddressDtoMapper;
import com.hello2morrow.ddaexample.business.contact.domain.Address;
import com.hello2morrow.ddaexample.business.contact.service.AddressDto;
import com.hello2morrow.ddaexample.business.customer.controller.CustomerDtoMapper;
import com.hello2morrow.ddaexample.business.customer.domain.Customer;
import com.hello2morrow.ddaexample.business.customer.service.CustomerDto;
import com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForInformation;
import com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForOffer;
import com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive;
import com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant;
import com.hello2morrow.ddaexample.business.distributionpartner.service.DistributionPartnerControllerServiceIf;
import com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForInformationDto;
import com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto;
import com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto;
import com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto;
import com.hello2morrow.ddaexample.business.user.controller.UserController;
import com.hello2morrow.ddaexample.business.user.service.ContextDto;

/**
 * @dda-generate-service
 */
public final class DistributionPartnerController implements DistributionPartnerControllerServiceIf
{
    private static Logger s_Logger = Logger.getLogger(DistributionPartnerController.class);

    /**
     * @dda-service ASSIGN_CUSTOMERS_TO_SALES_ASSISTANT_CMD = "DistributionPartner::AssignCustomersToSalesAssistantCmd"
     */
    public void assignCustomersToSalesAssistant(ContextDto contextDto, ObjectIdIf[] customerIds,
                    ObjectIdIf salesAssistantId) throws BusinessException, TechnicalException
    {
//        new Canvas();
    	//SetupFactories.initialize();
        UserController.checkPermission(contextDto,
                        DistributionPartnerControllerServiceIf.ASSIGN_CUSTOMERS_TO_SALES_ASSISTANT_CMD);
        assert AssertionUtility.checkArray(customerIds);
        assert salesAssistantId != null;

        SalesAssistant salesAssistant = (SalesAssistant) DomainObjectWithDataSupplier.findByObjectId(salesAssistantId);
        assert salesAssistant != null;

        for (int i = 0; i < customerIds.length; i++)
        {
            Customer customerToAssign = (Customer) DomainObjectWithDataSupplier.findByObjectId(customerIds[i]);
            assert customerToAssign != null;
            salesAssistant.addToCustomers(customerToAssign);
        }
    }

    /**
     * @dda-service RETRIEVE_ASSIGNED_CUSTOMERS_FOR_SALES_ASSISTANT_CMD = "DistributionPartner::RetrieveAssignedCustomersForSalesAssistantCmd"
     */
    public CustomerDto[] retrieveAssignedCustomersForSalesAssistant(ContextDto contextDto, ObjectIdIf salesAssistantId)
                    throws BusinessException, TechnicalException
    {
        UserController.checkPermission(contextDto,
                        DistributionPartnerControllerServiceIf.RETRIEVE_ASSIGNED_CUSTOMERS_FOR_SALES_ASSISTANT_CMD);
        assert salesAssistantId != null;

        SalesAssistant salesAssistant = (SalesAssistant) DomainObjectWithDataSupplier.findByObjectId(salesAssistantId);
        Customer[] assigned = salesAssistant.getCustomers();
        return CustomerDtoMapper.createDtosFromDomainObjects(assigned);
    }

    /**
     * @dda-service CREATE_SALES_ASSISTANT_CMD = "DistributionPartner::CreateSalesAssistantCmd"
     */
    public ObjectIdIf createSalesAssistant(ContextDto contextDto, SalesAssistantDto salesAssistantDto,
                    AddressDto addressDto) throws BusinessException, TechnicalException
    {
        UserController.checkPermission(contextDto, DistributionPartnerControllerServiceIf.CREATE_SALES_ASSISTANT_CMD);
        assert salesAssistantDto != null;
        assert addressDto != null;
        salesAssistantDto.validate(DistributionPartnerControllerServiceIf.class,
                        DistributionPartnerControllerServiceIf.CREATE_SALES_ASSISTANT_CMD);
        addressDto.validate(DistributionPartnerControllerServiceIf.class,
                        DistributionPartnerControllerServiceIf.CREATE_SALES_ASSISTANT_CMD);

        Address address = new Address(DomainObjectWithDataSupplier.TRANSIENT);
        AddressDtoMapper.mapDtoToDomainObject(addressDto, address, false);
        if (address.isValid())
        {
            address.save();
        }
        else
        {
            address.delete();
            throw new BusinessException("address not valid");
        }

        SalesAssistant[] found = SalesAssistant.findSalesAssistantByFirstNameAndLastName(
                        salesAssistantDto.getFirstName(), salesAssistantDto.getLastName());
        if (found.length > 0)
        {
            for (int i = 0; i < found.length; i++)
            {
                Address foundCustomerAddress = found[i].getAddress();
                assert foundCustomerAddress != null;
                if (foundCustomerAddress.isSameAddress(address))
                {
                    address.delete();
                    throw new BusinessException("sales assistant already exists - (firstname/lastname) = "
                                    + salesAssistantDto.getFirstName() + " " + salesAssistantDto.getLastName());
                }
            }
        }

        SalesAssistant salesAssistant = new SalesAssistant();
        SalesAssistantDtoMapper.mapDtoToDomainObject(salesAssistantDto, salesAssistant, false);
        salesAssistant.setAddress(address);

        return salesAssistant.getObjectId();
    }

    /**
     * @dda-service CREATE_REQUEST_FOR_INFORMATION_CMD = "DistributionPartner::CreateRequestForInformationCmd"
     */
    public ObjectIdIf createRequestForInformation(ContextDto contextDto, RequestForInformationDto requestDto)
                    throws BusinessException, TechnicalException
    {
        assert requestDto != null;
        UserController.checkPermission(contextDto,
                        DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_INFORMATION_CMD);
        requestDto.validate(DistributionPartnerControllerServiceIf.class,
                        DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_INFORMATION_CMD);
        RequestForInformation request = new RequestForInformation();
        RequestForInformationDtoMapper.mapDtoToDomainObject(requestDto, request, false);
        return request.getObjectId();
    }

    /**
     * @dda-service CREATE_REQUEST_FOR_OFFER_CMD = "DistributionPartner::CreateRequestForOfferCmd"
     */
    public ObjectIdIf createRequestForOffer(ContextDto contextDto, RequestForOfferDto requestDto)
                    throws BusinessException, TechnicalException
    {
        assert requestDto != null;
        UserController.checkPermission(contextDto, DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_OFFER_CMD);
        requestDto.validate(DistributionPartnerControllerServiceIf.class,
                        DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_OFFER_CMD);
        RequestForOffer request = new RequestForOffer();
        RequestForOfferDtoMapper.mapDtoToDomainObject(requestDto, request, false);
        return request.getObjectId();
    }

    /**
     * @dda-service CREATE_REQUEST_FOR_TEST_DRIVE_CMD = "DistributionPartner::CreateRequestForTestDriveCmd"
     */
    public ObjectIdIf createRequestForTestDrive(ContextDto contextDto, RequestForTestDriveDto requestDto)
                    throws BusinessException, TechnicalException
    {
        assert requestDto != null;
        UserController.checkPermission(contextDto,
                        DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_TEST_DRIVE_CMD);
        requestDto.validate(DistributionPartnerControllerServiceIf.class,
                        DistributionPartnerControllerServiceIf.CREATE_REQUEST_FOR_TEST_DRIVE_CMD);

        RequestForTestDrive request = new RequestForTestDrive();
        RequestForTestDriveDtoMapper.mapDtoToDomainObject(requestDto, request, false);
        return request.getObjectId();
    }
}