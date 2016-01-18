/**
 * 23.05.2005 17:28:18 - generated 'data-supplier-interface' for 'com.hello2morrow.ddaexample.business.user.domain.User'
 */

package com.hello2morrow.ddaexample.business.user.dsi;

public interface UserDsi extends com.hello2morrow.dda.business.common.dsi.DataSupplierIf
{
	public java.lang.String getName()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setName(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public java.lang.String getPassword()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setPassword(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public com.hello2morrow.ddaexample.business.user.dsi.RoleDsi[] getRoles()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setRoles(com.hello2morrow.ddaexample.business.user.dsi.RoleDsi[] all)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException,
			com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void addToRoles(com.hello2morrow.ddaexample.business.user.dsi.RoleDsi add)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException,
			com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void removeFromRoles(com.hello2morrow.ddaexample.business.user.dsi.RoleDsi remove)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException,
			com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}