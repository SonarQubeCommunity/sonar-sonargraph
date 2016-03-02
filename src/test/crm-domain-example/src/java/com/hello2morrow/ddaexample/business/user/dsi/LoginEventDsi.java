/**
 * 23.05.2005 17:28:18 - generated 'data-supplier-interface' for 'com.hello2morrow.ddaexample.business.user.domain.LoginEvent'
 */

package com.hello2morrow.ddaexample.business.user.dsi;

public interface LoginEventDsi extends com.hello2morrow.dda.business.common.dsi.DataSupplierIf
{
	public java.lang.String getUserName()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setUserName(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public java.lang.String getCreatedTimestamp()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setCreatedTimestamp(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}