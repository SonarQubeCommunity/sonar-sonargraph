package com.hello2morrow.dda.business.common.dsi;

import java.lang.reflect.Array;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.foundation.common.DateUtil;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.dda.foundation.common.exception.BusinessException;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

/**
 * transient or persistent domain object base class
 */
public abstract class DomainObjectWithDataSupplier extends DomainObject
{
    public static final int TRANSIENT = 0;
    public static final int PERSISTENT = 1;
    private static Logger s_Logger = Logger.getLogger(DomainObjectWithDataSupplier.class);
    private boolean m_IsPersistent;
    private DataSupplierReadMarker m_ReadMarker;

    protected static boolean makePersistent(int storageType)
    {
        assert storageType == TRANSIENT || storageType == PERSISTENT;
        if (storageType == TRANSIENT)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static DomainObjectWithDataSupplier findByObjectId(ObjectIdIf objectId) throws TechnicalException
    {
        assert objectId != null;
        assert objectId instanceof DomainObjectId;
        DataManagerIf dataManager = getDataManager(((DomainObjectId) objectId).getDataSupplierInterfaceClass());
        assert dataManager != null;
        return getDomainObject(dataManager.findByObjectId(objectId));
    }

    /**
     * Returns the registered persistence manager for the given data supplier
     * interface.
     * 
     * @param dataSupplierInterface
     * @return
     */
    protected static DataManagerIf getDataManager(Class dataSupplierInterface)
    {
        return DataManagerFactory.getInstance().getDataManagerImplementation(dataSupplierInterface);
    }

    /**
     * Creates an array of domain objects for the given persistence suppliers.
     * The domain objects that can not be found in the cache are created.
     * 
     * @param persistenceSupplier
     * @param domainClass runtime type of the returned array
     * @return array with references to the created domain objects
     */
    protected static DomainObjectWithDataSupplier[] getDomainObjects(DataSupplierIf[] dataSuppliers, Class domainClass)
    {
        assert dataSuppliers != null;
        assert AssertionUtility.checkUniqueElementsArray(dataSuppliers);
        assert domainClass != null;

        DomainObjectWithDataSupplier[] domainObjects = (DomainObjectWithDataSupplier[]) Array.newInstance(domainClass,
                        dataSuppliers.length);
        for (int i = 0; i < domainObjects.length; ++i)
        {
            domainObjects[i] = (DomainObjectWithDataSupplier) DomainObjectFactory.getInstance().get(dataSuppliers[i]);
        }

        return domainObjects;
    }

    /**
     * Creates a domain object (if not already in the cache) for the given
     * persistence data supplier
     * 
     * @param persistenceSupplier
     * @return the domain object
     */
    protected static DomainObjectWithDataSupplier getDomainObject(DataSupplierIf dataSupplier)
    {
        DomainObjectWithDataSupplier domainObject = null;

        if (dataSupplier != null)
        {
            domainObject = (DomainObjectWithDataSupplier) DomainObjectFactory.getInstance().get(dataSupplier);
        }

        return domainObject;
    }

    public static DataSupplierIf[] getDataSuppliers(DomainObject[] domainObjects, Class dataSupplierInterfaceClass)
    {
        assert AssertionUtility.checkUniqueElementsArray(domainObjects);
        assert dataSupplierInterfaceClass != null;

        DataSupplierIf[] suppliers = (DataSupplierIf[]) Array.newInstance(dataSupplierInterfaceClass,
                        domainObjects.length);

        for (int i = 0; i < domainObjects.length; ++i)
        {
            assert domainObjects[i].hasDataSupplier();
            suppliers[i] = domainObjects[i].getDataSupplier();
        }

        return suppliers;
    }

    /**
     * Used for creation from data supplier layer via the DomainObjectFactory  
     * @param dataSupplier
     * @param marker
     */
    public DomainObjectWithDataSupplier(DataSupplierIf dataSupplier, DataSupplierReadMarker marker)
    {
        super(dataSupplier);
        assert dataSupplier != null;
        assert marker != null;
        assert DataManagerFactory.getInstance().hasDataManagerImplementation(
                        ((DomainObjectId) dataSupplier.getObjectId()).getDataSupplierInterfaceClass());
        assert dataSupplier.supportsPersistentData();
        m_ReadMarker = marker;
        m_IsPersistent = true;
    }

    /**
     * Used for creation from subclasses
     * @param dataSupplier
     */
    public DomainObjectWithDataSupplier(DataSupplierIf dataSupplier)
    {
        super(dataSupplier);
        assert dataSupplier != null;
        assert DataManagerFactory.getInstance().hasDataManagerImplementation(
                        ((DomainObjectId) dataSupplier.getObjectId()).getDataSupplierInterfaceClass());
        if (dataSupplier.supportsPersistentData())
        {
            m_IsPersistent = true;
        }
    }

    public void delete() throws TechnicalException
    {
        assert !hasBeenDeleted();

        if (getDataSupplier().supportsPersistentData())
        {
            DataManagerIf dataManager = getDataManager(((DomainObjectId) getDataSupplier().getObjectId()).getDataSupplierInterfaceClass());
            dataManager.deleteDataSupplier(getDataSupplier());
        }
        else
        {
            markDeleted((DomainObjectId) getDataSupplier().getObjectId());
        }
    }

    public void save() throws BusinessException, TechnicalException
    {
        assert !hasBeenDeleted();
        DataSupplierIf dataSupplier = getDataSupplier();
        assert dataSupplier != null;
        assert !dataSupplier.supportsPersistentData();
        setDataSupplier(((TransientDataSupplier) dataSupplier).save());
        assert getDataSupplier().supportsPersistentData();
        m_IsPersistent = true;
    }

    public final boolean hasDataSupplier()
    {
        return true;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);

        if (m_IsPersistent)
        {
            assert hasDataSupplier();
            buffer.append("- persistent");
            buffer.append(LINE_SEPARATOR);
            if (m_ReadMarker != null)
            {
                buffer.append("- read from data supplier layer = ");
                buffer.append(DateUtil.convert(m_ReadMarker.getReadTimeStamp()));
                buffer.append(LINE_SEPARATOR);
            }
            else
            {
                buffer.append("- created in session");
                buffer.append(LINE_SEPARATOR);
            }
        }
        else
        {
            buffer.append("- transient");
            buffer.append(LINE_SEPARATOR);
        }

        buffer.append("- with data supplier");

        return buffer.toString();
    }
}