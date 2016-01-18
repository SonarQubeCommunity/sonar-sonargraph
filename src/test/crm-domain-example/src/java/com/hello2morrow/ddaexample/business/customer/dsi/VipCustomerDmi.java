/**
 * 23.05.2005 17:28:20 - generated 'data-manager-interface' for 'com.hello2morrow.ddaexample.business.customer.domain.VipCustomer'
 */

package com.hello2morrow.ddaexample.business.customer.dsi;

public interface VipCustomerDmi extends com.hello2morrow.ddaexample.business.customer.dsi.CustomerDmi
{
	public com.hello2morrow.ddaexample.business.customer.dsi.VipCustomerDsi[] findVipCustomerByFirstNameAndLastName(java.lang.String firstName, java.lang.String lastName)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}