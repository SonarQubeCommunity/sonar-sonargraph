/**
 * 23.05.2005 17:28:19 - generated 'data-supplier-transient' for 'com.hello2morrow.ddaexample.business.contact.domain.Person'
 */

package com.hello2morrow.ddaexample.business.contact.data.trans;

import com.hello2morrow.ddaexample.business.contact.dsi.PersonDsi;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DataManagerIf;
import com.hello2morrow.dda.business.common.dsi.DomainObjectId;
import com.hello2morrow.dda.business.common.dsi.DataSupplierIf;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public class PersonDataSupplier extends com.hello2morrow.dda.business.common.dsi.TransientDataSupplier implements PersonDsi
{
	protected PersonDataSupplier(DomainObjectId id)
	{
		super(id);
	}

	private java.lang.String m_FirstName = null; 

	public java.lang.String getFirstName()
	{
		return m_FirstName;
	}

	public void setFirstName(java.lang.String set)
	{
		m_FirstName = set;
	}

	private java.lang.String m_LastName = null; 

	public java.lang.String getLastName()
	{
		return m_LastName;
	}

	public void setLastName(java.lang.String set)
	{
		m_LastName = set;
	}

	private com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi m_Address = null; 

	public com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi getAddress()
	{
		return m_Address;
	}

	public void setAddress(com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi set)
	{
		m_Address = set;
	}

    protected DataSupplierIf createPersistentDataSupplier() throws TechnicalException
	{
        DataManagerIf dmi = DataManagerFactory.getInstance().getDataManagerImplementation(PersonDsi.class);
        assert dmi != null;
        PersonDsi created = (PersonDsi) dmi.createDataSupplier(PersonDsi.class, true);
		return created;
	}
	
	protected void mapContentTo(DataSupplierIf persistent) throws BusinessException, TechnicalException
	{
		super.mapContentTo(persistent);

		PersonDsi concrete = (PersonDsi)persistent;
		
		concrete.setFirstName(getFirstName());
		concrete.setLastName(getLastName());
		com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi address = getAddress();
		if(!address.supportsPersistentData())
		{
			concrete.setAddress((com.hello2morrow.ddaexample.business.contact.dsi.AddressDsi)((com.hello2morrow.dda.business.common.dsi.TransientDataSupplier)address).save());
		}
		else
		{
			concrete.setAddress(address); 
		}
	}
}