/**
 * 23.05.2005 17:28:21 - generated 'data-transfer-object' for 'com.hello2morrow.ddaexample.business.distributionpartner.domain.RequestForTestDrive'
 */

package com.hello2morrow.ddaexample.business.distributionpartner.service;

public class RequestForTestDriveDto extends com.hello2morrow.ddaexample.business.request.service.RequestDto
{
	private java.util.Date m_TargetDate;
	
	public java.util.Date getTargetDate()
	{
		return m_TargetDate;	
	}
	
	public void setTargetDate(java.util.Date targetDate)
	{
		m_TargetDate = targetDate;	
	}

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

		buffer.append("TargetDate = ");
		buffer.append(getTargetDate());
        buffer.append(LINE_SEPARATOR);
	
        return buffer.toString();
    }
}