/**
 * 23.05.2005 17:28:20 - generated 'data-manager-interface' for 'com.hello2morrow.ddaexample.business.request.domain.Request'
 */

package com.hello2morrow.ddaexample.business.request.dsi;

public interface RequestDmi extends com.hello2morrow.dda.business.common.dsi.DataManagerIf
{
	public com.hello2morrow.ddaexample.business.request.dsi.RequestDsi[] findAllRequests()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}