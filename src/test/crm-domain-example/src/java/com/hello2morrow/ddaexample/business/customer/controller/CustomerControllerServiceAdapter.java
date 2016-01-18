/**
 * 23.05.2005 17:28:17 - generated 'service-adapter' for 'com.hello2morrow.ddaexample.business.customer.controller.CustomerController'
 */

package com.hello2morrow.ddaexample.business.customer.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.dsi.DomainObjectFactory;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class CustomerControllerServiceAdapter implements com.hello2morrow.ddaexample.business.customer.service.CustomerControllerServiceIf
{
    private final static Logger s_Logger = Logger.getLogger(CustomerControllerServiceAdapter.class);
    private final CustomerController m_Controller;

    public CustomerControllerServiceAdapter()
    {
        m_Controller = new CustomerController();
    }

	public com.hello2morrow.dda.foundation.common.ObjectIdIf createCustomer(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.customer.service.CustomerDto customerDto, com.hello2morrow.ddaexample.business.contact.service.AddressDto addressDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.dda.foundation.common.ObjectIdIf result = m_Controller.createCustomer(contextDto, customerDto, addressDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createVipCustomer(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.customer.service.VipCustomerDto customerDto, com.hello2morrow.ddaexample.business.contact.service.AddressDto addressDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.dda.foundation.common.ObjectIdIf result = m_Controller.createVipCustomer(contextDto, customerDto, addressDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
	public com.hello2morrow.ddaexample.business.customer.service.CustomerDto[] retrieveCustomers(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.ddaexample.business.customer.service.CustomerDto[] result = m_Controller.retrieveCustomers(contextDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
}