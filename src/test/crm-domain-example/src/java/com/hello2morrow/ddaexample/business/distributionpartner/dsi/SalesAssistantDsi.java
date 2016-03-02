/**
 * 23.05.2005 17:28:22 - generated 'data-supplier-interface' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.dsi;

public interface SalesAssistantDsi extends com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi
{
	public com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[] getCustomers()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setCustomers(com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[] all)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException,
			com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void addToCustomers(com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi add)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException,
			com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void removeFromCustomers(com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi remove)
		throws com.hello2morrow.dda.foundation.common.exception.BusinessException,
			com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}