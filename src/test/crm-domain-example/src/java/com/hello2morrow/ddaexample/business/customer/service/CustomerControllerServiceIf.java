/**
 * 23.05.2005 17:28:17 - generated 'service-interface' for 'com.hello2morrow.ddaexample.business.customer.controller.CustomerController'
 */

package com.hello2morrow.ddaexample.business.customer.service;

import com.hello2morrow.dda.business.common.service.ServiceIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public interface CustomerControllerServiceIf extends ServiceIf
{
	public final static String CREATE_CUSTOMER_CMD = "Customer::CreateCustomerCmd";
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createCustomer(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.customer.service.CustomerDto customerDto, com.hello2morrow.ddaexample.business.contact.service.AddressDto addressDto) 
		throws BusinessException, TechnicalException;

	public final static String CREATE_VIPCUSTOMER_CMD = "Customer::CreateVipCustomerCmd";
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createVipCustomer(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto customerDto, com.hello2morrow.ddaexample.business.contact.service.AddressDto addressDto) 
		throws BusinessException, TechnicalException;

	public final static String RETRIEVE_CUSTOMERS_CMD = "Customer::RetrieveCustomersCmd";
	public com.hello2morrow.ddaexample.business.customer.service.CustomerDto[] retrieveCustomers(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto) 
		throws BusinessException, TechnicalException;

}