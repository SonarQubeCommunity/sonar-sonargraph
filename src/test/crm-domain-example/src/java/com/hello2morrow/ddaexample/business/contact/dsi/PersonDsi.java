/**
 * 23.05.2005 17:28:19 - generated 'data-supplier-interface' for 'com.hello2morrow.ddaexample.business.contact.domain.Person'
 */

package com.hello2morrow.ddaexample.business.contact.dsi;

public interface PersonDsi extends com.hello2morrow.dda.business.common.dsi.DataSupplierIf
{
	public java.lang.String getFirstName()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setFirstName(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public java.lang.String getLastName()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setLastName(java.lang.String all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi getAddress()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setAddress(com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}