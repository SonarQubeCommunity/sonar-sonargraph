package com.hello2morrow.dda.business.common.dsi;

import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;


public interface DataManagerIf
{
    /**
     * Every manager implementation must be able to create a data supplier for
     * a 'new' domain object.
     * @param dataSupplierInterfaceClass
     * @param isPersistent
     * @return the created
     * @throws TechnicalException
     */
    public abstract DataSupplierIf createDataSupplier(Class dataSupplierInterfaceClass, boolean isPersistent)
                    throws TechnicalException;

    /**
     * Every manager implementation must be able to delete a given data supplier. This may result
     * in the deletion of other data suppliers. 
     * @param aDataSupplier - data supplier for which the deletion has been invoked. 
     * @throws TechnicalException
     */
    public void deleteDataSupplier(DataSupplierIf dataSupplier) throws TechnicalException;

    /**
     * Every persistence manager implementation must be able to create a data supplier for
     * a specified object id.
     * @param id.
     * @throws TechnicalException If a technical error occurs during data load.
     */
    public DataSupplierIf findByObjectId(ObjectIdIf id) throws TechnicalException;
}