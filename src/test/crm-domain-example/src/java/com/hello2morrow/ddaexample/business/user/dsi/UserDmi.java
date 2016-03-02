/**
 * 23.05.2005 17:28:18 - generated 'data-manager-interface' for 'com.hello2morrow.ddaexample.business.user.domain.User'
 */

package com.hello2morrow.ddaexample.business.user.dsi;

public interface UserDmi extends com.hello2morrow.dda.business.common.dsi.DataManagerIf
{
	public com.hello2morrow.ddaexample.business.user.dsi.UserDsi[] findAllUsers()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public com.hello2morrow.ddaexample.business.user.dsi.UserDsi findUserByName(java.lang.String name)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}