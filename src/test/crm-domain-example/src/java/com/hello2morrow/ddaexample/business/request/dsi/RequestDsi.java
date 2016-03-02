/**
 * 23.05.2005 17:28:20 - generated 'data-supplier-interface' for 'com.hello2morrow.ddaexample.business.request.domain.Request'
 */

package com.hello2morrow.ddaexample.business.request.dsi;

public interface RequestDsi extends com.hello2morrow.dda.business.common.dsi.DataSupplierIf
{
	public java.lang.String getSubject()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setSubject(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public java.lang.String getCreatedTimestamp()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setCreatedTimestamp(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public com.hello2morrow.ddaexample.business.request.dsi.StateDsi getState()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setState(com.hello2morrow.ddaexample.business.request.dsi.StateDsi all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}