/**
 * 23.05.2005 17:28:17 - generated 'data-supplier-interface' for 'com.hello2morrow.ddaexample.business.user.domain.Role'
 */

package com.hello2morrow.ddaexample.business.user.dsi;

public interface RoleDsi extends com.hello2morrow.dda.business.common.dsi.DataSupplierIf
{
	public java.lang.String getName()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setName(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[] getServerCommands()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setServerCommands(com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[] all)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException,
			com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void addToServerCommands(com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi add)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException,
			com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void removeFromServerCommands(com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi remove)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException,
			com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}