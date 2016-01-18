/**
 * 23.05.2005 17:28:18 - generated 'service-adapter' for 'com.hello2morrow.ddaexample.business.distributionpartner.controller.DistributionPartnerController'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.dsi.DomainObjectFactory;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class DistributionPartnerControllerServiceAdapter implements com.hello2morrow.ddaexample.business.distributionpartner.service.DistributionPartnerControllerServiceIf
{
    private final static Logger s_Logger = Logger.getLogger(DistributionPartnerControllerServiceAdapter.class);
    private final DistributionPartnerController m_Controller;

    public DistributionPartnerControllerServiceAdapter()
    {
//        new JBStartUpMBeanImpl();
        m_Controller = new DistributionPartnerController();
    }

	public void assignCustomersToSalesAssistant(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.dda.foundation.common.ObjectIdIf[] customerIds, com.hello2morrow.dda.foundation.common.ObjectIdIf salesAssistantId) throws BusinessException, TechnicalException
	{
		m_Controller.assignCustomersToSalesAssistant(contextDto, customerIds, salesAssistantId);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
	}
	
	public com.hello2morrow.ddaexample.business.customer.service.CustomerDto[] retrieveAssignedCustomersForSalesAssistant(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.dda.foundation.common.ObjectIdIf salesAssistantId) throws BusinessException, TechnicalException
	{
		com.hello2morrow.ddaexample.business.customer.service.CustomerDto[] result = m_Controller.retrieveAssignedCustomersForSalesAssistant(contextDto, salesAssistantId);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createSalesAssistant(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto salesAssistantDto, com.hello2morrow.ddaexample.business.contact.service.AddressDto addressDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.dda.foundation.common.ObjectIdIf result = m_Controller.createSalesAssistant(contextDto, salesAssistantDto, addressDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createRequestForInformation(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForInformationDto requestDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.dda.foundation.common.ObjectIdIf result = m_Controller.createRequestForInformation(contextDto, requestDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createRequestForOffer(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto requestDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.dda.foundation.common.ObjectIdIf result = m_Controller.createRequestForOffer(contextDto, requestDto);
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
        DomainObjectFactory.getInstance().clearCache();
        return result;
	}
	
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createRequestForTestDrive(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto requestDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.dda.foundation.common.ObjectIdIf result = m_Controller.createRequestForTestDrive(contextDto, requestDto);
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