package com.hello2morrow.dda.business.common.dsi;

import com.hello2morrow.dda.foundation.common.ObjectIdIf;

/**
 * A class that delievers data for a domain object implements this interface
 */
public interface DataSupplierIf
{
    public static String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Every implementation must be able to return the object id. The id is used to compare
     * domain objects.
     * @return The id.  
     */
    public ObjectIdIf getObjectId();

    /**
     * Every implementation must be able to update the object id. Used for version updates.
     * @return The id.  
     */
    public void updateObjectId(ObjectIdIf objectId);

    /**
     * Sets the domain object that is associated with the data interface
     * implementation. Should be used to mark a domain object as deleted 
     * (for testing/debugging purposes).
     * @param domainObject The domain object that is associated with the 
     * interface implementation. 
     */
    public void usedByDomainObject(DomainObjectIf domainObject);

    public boolean supportsPersistentData();

    public DomainObjectIf getDomainObject();
}