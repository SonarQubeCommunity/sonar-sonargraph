/**
 * 23.05.2005 17:28:22 - generated 'data-manager-interface' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.SalesAssistant'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.dsi;

public interface SalesAssistantDmi extends com.hello2morrow.ddaexample.business.contact.dsi.PersonDmi
{
	public com.hello2morrow.ddaexample.business.distributionpartner.dsi.SalesAssistantDsi[] findSalesAssistantByFirstNameAndLastName(java.lang.String firstName, java.lang.String lastName)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}