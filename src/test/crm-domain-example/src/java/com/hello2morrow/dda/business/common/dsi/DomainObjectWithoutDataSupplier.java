package com.hello2morrow.dda.business.common.dsi;

/**
 * transient domain object without data supplier base class
 */
public abstract class DomainObjectWithoutDataSupplier extends DomainObject
{
    public DomainObjectWithoutDataSupplier()
    {
        super(new EmptyDataSupplier());
        assert !DataManagerFactory.getInstance().hasDataManagerImplementation(EmptyDataSupplier.class);
    }

    public final boolean hasDataSupplier()
    {
        return false;
    }

    public void delete()
    {
        markDeleted((DomainObjectId) getDataSupplier().getObjectId());
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);
        buffer.append("- transient");
        buffer.append(LINE_SEPARATOR);
        buffer.append("- without data supplier");
        return buffer.toString();
    }
}