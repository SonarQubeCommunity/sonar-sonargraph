/**
 * 23.05.2005 17:28:20 - generated 'service-interface' for 'com.hello2morrow.ddaexample.business.request.controller.RequestController'
 */

package com.hello2morrow.ddaexample.business.request.service;

import com.hello2morrow.dda.business.common.service.ServiceIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public interface RequestControllerServiceIf extends ServiceIf
{
	public final static String RETRIEVE_REQUESTS_CMD = "Request::RetrieveRequestsCmd";
	public com.hello2morrow.ddaexample.business.request.service.RequestDto[] retrieveRequests(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto) 
		throws BusinessException, TechnicalException;

}