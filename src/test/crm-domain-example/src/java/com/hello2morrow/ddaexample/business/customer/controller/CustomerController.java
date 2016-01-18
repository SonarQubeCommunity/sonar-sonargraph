package com.hello2morrow.ddaexample.business.customer.controller;

import com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
import com.hello2morrow.ddaexample.business.contact.controller.AddressDtoMapper;
import com.hello2morrow.ddaexample.business.contact.domain.Address;
import com.hello2morrow.ddaexample.business.contact.service.AddressDto;
import com.hello2morrow.ddaexample.business.customer.domain.Customer;
import com.hello2morrow.ddaexample.business.customer.domain.VipCustomer;
import com.hello2morrow.ddaexample.business.customer.service.CustomerControllerServiceIf;
import com.hello2morrow.ddaexample.business.customer.service.CustomerDto;
import com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto;
import com.hello2morrow.ddaexample.business.user.controller.UserController;
import com.hello2morrow.ddaexample.business.user.service.ContextDto;

/**
 * @dda-generate-service
 */
public final class CustomerController implements CustomerControllerServiceIf
{
    /**
     * @dda-service CREATE_CUSTOMER_CMD = "Customer::CreateCustomerCmd"
     */
    public ObjectIdIf createCustomer(ContextDto contextDto, CustomerDto customerDto, AddressDto addressDto)
                    throws BusinessException, TechnicalException
    {
        UserController.checkPermission(contextDto, CustomerControllerServiceIf.CREATE_CUSTOMER_CMD);
        assert customerDto != null;
        assert addressDto != null;
        customerDto.validate(CustomerControllerServiceIf.class, CustomerControllerServiceIf.CREATE_CUSTOMER_CMD);
        addressDto.validate(CustomerControllerServiceIf.class, CustomerControllerServiceIf.CREATE_CUSTOMER_CMD);

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

        Customer[] found = Customer.findCustomerByFirstNameAndLastName(customerDto.getFirstName(),
                        customerDto.getLastName());
        if (found.length > 0)
        {
            for (int i = 0; i < found.length; i++)
            {
                Address foundCustomerAddress = found[i].getAddress();
                assert foundCustomerAddress != null;
                if (foundCustomerAddress.isSameAddress(address))
                {
                    address.delete();
                    throw new BusinessException("customer already exits - (firstname/lastname) = "
                                    + customerDto.getFirstName() + " " + customerDto.getLastName());
                }
            }
        }

        Customer customer = new Customer();
        CustomerDtoMapper.mapDtoToDomainObject(customerDto, customer, false);
        customer.setAddress(address);

        return customer.getObjectId();
    }

    /**
     * @dda-service CREATE_VIPCUSTOMER_CMD = "Customer::CreateVipCustomerCmd"
     */
    public ObjectIdIf createVipCustomer(ContextDto contextDto, VipCustomerDto customerDto, AddressDto addressDto)
                    throws BusinessException, TechnicalException
    {
        UserController.checkPermission(contextDto, CustomerControllerServiceIf.CREATE_VIPCUSTOMER_CMD);
        assert customerDto != null;
        assert addressDto != null;
        customerDto.validate(CustomerControllerServiceIf.class, CustomerControllerServiceIf.CREATE_VIPCUSTOMER_CMD);
        addressDto.validate(CustomerControllerServiceIf.class, CustomerControllerServiceIf.CREATE_VIPCUSTOMER_CMD);

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

        VipCustomer[] found = VipCustomer.findVipCustomerByFirstNameAndLastName(customerDto.getFirstName(),
                        customerDto.getLastName());
        if (found.length > 0)
        {
            for (int i = 0; i < found.length; i++)
            {
                Address foundCustomerAddress = found[i].getAddress();
                assert foundCustomerAddress != null;
                if (foundCustomerAddress.isSameAddress(address))
                {
                    address.delete();
                    throw new BusinessException("vip customer already exits - (firstname/lastname) = "
                                    + customerDto.getFirstName() + " " + customerDto.getLastName());
                }
            }
        }

        VipCustomer customer = new VipCustomer();
        VipCustomerDtoMapper.mapDtoToDomainObject(customerDto, customer, false);
        customer.setAddress(address);

        return customer.getObjectId();
    }

    /**
     * @dda-service RETRIEVE_CUSTOMERS_CMD = "Customer::RetrieveCustomersCmd"
     */
    public CustomerDto[] retrieveCustomers(ContextDto contextDto) throws BusinessException, TechnicalException
    {
        UserController.checkPermission(contextDto, CustomerControllerServiceIf.RETRIEVE_CUSTOMERS_CMD);
        Customer[] customers = Customer.findAllCustomers();
        return CustomerDtoMapper.createDtosFromDomainObjects(customers);
    }
}