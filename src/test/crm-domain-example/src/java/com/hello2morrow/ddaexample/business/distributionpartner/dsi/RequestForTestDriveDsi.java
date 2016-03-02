/**
 * 23.05.2005 17:28:21 - generated 'data-supplier-interface' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.dsi;

public interface RequestForTestDriveDsi extends com.hello2morrow.ddaexample.business.request.dsi.RequestDsi
{
	public java.util.Date getTargetDate()
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

	public void setTargetDate(java.util.Date all)
		throws com.hello2morrow.dda.foundation.common.exception.TechnicalException;

}