/**
 * 23.05.2005 17:28:20 - generated 'data-supplier-interface' for 'com.hello2morrow.ddaexample.business.contact.domain.Address'
 */

package com.hello2morrow.ddaexample.business.contact.dsi;

public interface AddressDsi extends com.hello2morrow.dda.business.common.dsi.DataSupplierIf
{
	public java.lang.String getStreet()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setStreet(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public java.lang.String getCity()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setCity(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public java.lang.String getZipCode()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setZipCode(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}