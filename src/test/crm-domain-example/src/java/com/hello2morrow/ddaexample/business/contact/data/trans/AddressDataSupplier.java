/**
 * 23.05.2005 17:28:21 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.contact.domain.Address'
 */

package com.hello2morrow.ddaexample.business.contact.data.trans;

import com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class AddressDataSupplier extends com.hello2morrow.dda.business.common.dsi.TransientDataSupplier implements AddressDsi
{
	protected AddressDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	public AddressDataSupplier()
	{
		super(new DomainObjectId(AddressDsi.class));
	}

	private java.lang.String m_Street = null; 

	public java.lang.String getStreet()
	{
		return m_Street;
	}

	public void setStreet(java.lang.String set)
	{
		m_Street = set;
	}

	private java.lang.String m_City = null; 

	public java.lang.String getCity()
	{
		return m_City;
	}

	public void setCity(java.lang.String set)
	{
		m_City = set;
	}

	private java.lang.String m_ZipCode = null; 

	public java.lang.String getZipCode()
	{
		return m_ZipCode;
	}

	public void setZipCode(java.lang.String set)
	{
		m_ZipCode = set;
	}

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(AddressDsi.class);
        assert dmi != null;
        AddressDsi created = (AddressDsi) dmi.createDataSupplier(AddressDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		AddressDsi concrete = (AddressDsi)persistent;
		
		concrete.setStreet(getStreet());
		concrete.setCity(getCity());
		concrete.setZipCode(getZipCode());
	}
}