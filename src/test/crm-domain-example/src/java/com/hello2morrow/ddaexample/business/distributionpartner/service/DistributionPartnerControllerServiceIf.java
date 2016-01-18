/**
 * 23.05.2005 17:28:17 - generated 'service-interface' for 'com.hello2morrow.ddaexample.business.distributionpartner.controller.DistributionPartnerController'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.service;

import com.hello2morrow.dda.business.common.service.ServiceIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public interface DistributionPartnerControllerServiceIf extends ServiceIf
{
	public final static String ASSIGN_CUSTOMERS_TO_SALES_ASSISTANT_CMD = "DistributionPartner::AssignCustomersToSalesAssistantCmd";
	public void assignCustomersToSalesAssistant(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.dda.foundation.common.ObjectIdIf[] customerIds, com.hello2morrow.dda.foundation.common.ObjectIdIf salesAssistantId) 
		throws BusinessException, TechnicalException;

	public final static String RETRIEVE_ASSIGNED_CUSTOMERS_FOR_SALES_ASSISTANT_CMD = "DistributionPartner::RetrieveAssignedCustomersForSalesAssistantCmd";
	public com.hello2morrow.ddaexample.business.customer.service.CustomerDto[] retrieveAssignedCustomersForSalesAssistant(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.dda.foundation.common.ObjectIdIf salesAssistantId) 
		throws BusinessException, TechnicalException;

	public final static String CREATE_SALES_ASSISTANT_CMD = "DistributionPartner::CreateSalesAssistantCmd";
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createSalesAssistant(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.distributionpartner.service.SalesAssistantDto salesAssistantDto, com.hello2morrow.ddaexample.business.contact.service.AddressDto addressDto) 
		throws BusinessException, TechnicalException;

	public final static String CREATE_REQUEST_FOR_INFORMATION_CMD = "DistributionPartner::CreateRequestForInformationCmd";
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createRequestForInformation(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForInformationDto requestDto) 
		throws BusinessException, TechnicalException;

	public final static String CREATE_REQUEST_FOR_OFFER_CMD = "DistributionPartner::CreateRequestForOfferCmd";
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createRequestForOffer(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForOfferDto requestDto) 
		throws BusinessException, TechnicalException;

	public final static String CREATE_REQUEST_FOR_TEST_DRIVE_CMD = "DistributionPartner::CreateRequestForTestDriveCmd";
	public com.hello2morrow.dda.foundation.common.ObjectIdIf createRequestForTestDrive(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto, com.hello2morrow.ddaexample.business.distributionpartner.service.RequestForTestDriveDto requestDto) 
		throws BusinessException, TechnicalException;

}