/**
 * 23.05.2005 17:28:18 - generated 'data-manager-interface' for 'com.hello2morrow.ddaexample.business.user.domain.LoginEvent'
 */

package com.hello2morrow.ddaexample.business.user.dsi;

public interface LoginEventDmi extends com.hello2morrow.dda.business.common.dsi.DataManagerIf
{
	public com.hello2morrow.ddaexample.business.user.dsi.LoginEventDsi[] findAllLoginEvents()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}