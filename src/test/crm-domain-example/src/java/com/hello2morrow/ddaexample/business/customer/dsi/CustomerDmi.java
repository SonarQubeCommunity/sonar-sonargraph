/**
 * 23.05.2005 17:28:21 - generated 'data-manager-interface' for 'com.hello2morrow.ddaexample.business.customer.domain.Customer'
 */

package com.hello2morrow.ddaexample.business.customer.dsi;

public interface CustomerDmi extends com.hello2morrow.ddaexample.business.contact.dsi.PersonDmi
{
	public com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[] findAllCustomers()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public com.hello2morrow.ddaexample.business.customer.dsi.CustomerDsi[] findCustomerByFirstNameAndLastName(java.lang.String firstName, java.lang.String lastName)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}