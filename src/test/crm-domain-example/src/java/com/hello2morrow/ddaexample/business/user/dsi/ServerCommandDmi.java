/**
 * 23.05.2005 17:28:15 - generated 'data-manager-interface' for 'com.hello2morrow.ddaexample.business.user.domain.ServerCommand'
 */

package com.hello2morrow.ddaexample.business.user.dsi;

public interface ServerCommandDmi extends com.hello2morrow.dda.business.common.dsi.DataManagerIf
{
	public com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi[] findAllServerCommands()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi findServerCommandByName(java.lang.String name)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}