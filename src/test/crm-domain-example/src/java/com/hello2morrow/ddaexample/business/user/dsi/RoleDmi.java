/**
 * 23.05.2005 17:28:17 - generated 'data-manager-interface' for 'com.hello2morrow.ddaexample.business.user.domain.Role'
 */

package com.hello2morrow.ddaexample.business.user.dsi;

public interface RoleDmi extends com.hello2morrow.dda.business.common.dsi.DataManagerIf
{
	public com.hello2morrow.ddaexample.business.user.dsi.RoleDsi[] findAllRoles()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public com.hello2morrow.ddaexample.business.user.dsi.RoleDsi findRoleByName(java.lang.String name)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}