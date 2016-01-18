/**
 * 23.05.2005 17:28:20 - generated 'service-adapter' for 'com.hello2morrow.ddaexample.business.request.controller.RequestController'
 */

package com.hello2morrow.ddaexample.business.request.controller;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.dsi.DomainObjectFactory;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;
 
public final class RequestControllerServiceAdapter implements com.hello2morrow.ddaexample.business.request.service.RequestControllerServiceIf
{
    private final static Logger s_Logger = Logger.getLogger(RequestControllerServiceAdapter.class);
    private final RequestController m_Controller;

    public RequestControllerServiceAdapter()
    {
        m_Controller = new RequestController();
    }

	public com.hello2morrow.ddaexample.business.request.service.RequestDto[] retrieveRequests(com.hello2morrow.ddaexample.business.user.service.ContextDto contextDto) throws BusinessException, TechnicalException
	{
		com.hello2morrow.ddaexample.business.request.service.RequestDto[] result = m_Controller.retrieveRequests(contextDto);
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