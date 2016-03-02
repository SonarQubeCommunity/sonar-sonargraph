/**
 * 23.05.2005 17:28:21 - generated 'data-supplier-interface' for 'com.hello2morrow.ddaexample.business.customer.domain.Customer'
 */

package com.hello2morrow.ddaexample.business.customer.dsi;

public interface CustomerDsi extends com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi
{
	public int getAge()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setAge(int all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}