package com.hello2morrow.dda.business.common.dsi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;

final class Cache
{
    private final Map m_ObjectIdToDomainObject = new HashMap();

    Cache()
    {
        //Just to make the ctor package-scope
    }

    DomainObjectIf[] getDomainObjects()
    {
        return (DomainObjectIf[]) m_ObjectIdToDomainObject.values().toArray(new DomainObjectIf[0]);
    }

    void clear()
    {
        m_ObjectIdToDomainObject.clear();
    }

    void remove(DomainObjectId id)
    {
        assert id != null;
        DomainObjectIf domainObject = (DomainObjectIf) m_ObjectIdToDomainObject.remove(id);
        assert domainObject != null : "no domain object found with object id = " + id;
    }

    DomainObjectIf get(DomainObjectId id)
    {
        assert id != null;
        return (DomainObjectIf) m_ObjectIdToDomainObject.get(id);
    }

    void put(DomainObjectId id, DomainObjectIf domainObject)
    {
        assert id != null;
        assert domainObject != null;
        Object previous = m_ObjectIdToDomainObject.put(id, domainObject);
        assert previous == null : "another domain object is already registered with the same object id (" + id + ")";
    }
}

final class ThreadLocalCache extends ThreadLocal
{
    protected synchronized Object initialValue()
    {
        return new Cache();
    }
}

/**
 * Creates domain objects for given data supplier interface implementors.
 */

public final class DomainObjectFactory
{
    private static Logger s_Logger = Logger.getLogger(DomainObjectFactory.class);
    private static DomainObjectFactory s_Instance = null;
    private static final Object[] s_CtorArgs = new Object[2];
    private final Map m_DataSupplierInterfaceClassToCtor = new HashMap();
    private final ThreadLocalCache m_DomainObjectCache = new ThreadLocalCache();

    public static synchronized void createInstance(String properties) throws IOException
    {
        assert properties != null;
        assert properties.length() > 0;
        InputStream in = DomainObjectFactory.class.getResourceAsStream(properties);
        if (in == null)
        {
            throw new MissingResourceException("properties file not found = " + properties,
                            DomainObjectFactory.class.getName(), properties);
        }
        Properties loadedProperties = new Properties();
        loadedProperties.load(in);
        createInstance(loadedProperties);
    }

    public static synchronized void createInstance(Properties properties)
    {
        assert s_Instance == null : "domain object factory has already been created";
        s_Instance = new DomainObjectFactory(properties);
    }

    public static DomainObjectFactory getInstance()
    {
        assert s_Instance != null : "domain object factory has not been created";
        return s_Instance;
    }

    public static synchronized void deleteInstance()
    {
        assert s_Instance != null : "domain object factory has not been created";
        s_Instance.clearCache();
        s_Instance = null;
    }

    private DomainObjectFactory(Properties properties)
    {
        assert properties != null;

        Enumeration enumKeys = properties.keys();
        while (enumKeys.hasMoreElements())
        {
            String key = (String) enumKeys.nextElement();
            String value = properties.getProperty(key);
            createEntry(key.trim(), value.trim());
        }
    }

    private Cache getCache()
    {
        return (Cache) m_DomainObjectCache.get();
    }

    private void createEntry(String domainObjectClassName, String dataSupplierInterfaceName)
    {
        assert domainObjectClassName != null;
        assert domainObjectClassName.length() > 0;
        assert dataSupplierInterfaceName != null;
        assert dataSupplierInterfaceName.length() > 0;

        s_Logger.debug("trying to register ctor (domain object class name = data supplier interface class name) = "
                        + domainObjectClassName + " = " + dataSupplierInterfaceName);

        Class dataSupplierInterfaceClass = getDataSupplierInterfaceClass(dataSupplierInterfaceName);
        Class domainObjectClass = getDomainObjectClass(domainObjectClassName);

        assert dataSupplierInterfaceClass != null;
        assert domainObjectClass != null;

        Class[] args = new Class[2];
        args[0] = dataSupplierInterfaceClass;
        args[1] = DataSupplierReadMarker.class;

        Constructor ctor;

        try
        {
            ctor = domainObjectClass.getConstructor(args);
        }
        catch (NoSuchMethodException e)
        {
            s_Logger.error("constructor for class " + domainObjectClassName + " could not be found - "
                            + ExceptionUtility.collectAll(e));
            assert false : "constructor undefined: " + domainObjectClassName + "(" + dataSupplierInterfaceName + ")"
                            + ExceptionUtility.collectAll(e);
            return;
        }

        Object previous = m_DataSupplierInterfaceClassToCtor.put(dataSupplierInterfaceClass, ctor);

        if (previous != null)
        {
            s_Logger.error("duplicate data supplier interface key " + dataSupplierInterfaceName
                            + " found for domain object class " + domainObjectClassName);
            assert false : "duplicate data supplier interface key " + dataSupplierInterfaceName
                            + " found for domain object class " + domainObjectClassName;
        }

        s_Logger.debug("constructor loaded");
    }

    private Class getDomainObjectClass(String domainObjectClassName)
    {
        assert domainObjectClassName != null;
        assert domainObjectClassName.length() > 0;

        try
        {
            return Class.forName(domainObjectClassName);
        }
        catch (ClassNotFoundException e)
        {
            s_Logger.error("domain class " + domainObjectClassName + " could not be found - "
                            + ExceptionUtility.collectAll(e));
            assert false : "domain class " + domainObjectClassName + " could not be found";
            return null;
        }
    }

    private Class getDataSupplierInterfaceClass(String dataSupplierInterfaceName)
    {
        assert dataSupplierInterfaceName != null;
        assert dataSupplierInterfaceName.length() > 0;

        try
        {
            return Class.forName(dataSupplierInterfaceName);
        }
        catch (ClassNotFoundException e)
        {
            s_Logger.error("data supplier interface " + dataSupplierInterfaceName + " could not be found - "
                            + ExceptionUtility.collectAll(e));
            assert false : "data supplier interface " + dataSupplierInterfaceName + " could not be found";
            return null;
        }
    }

    public void clearCache()
    {
        getCache().clear();
    }

    public DomainObjectIf[] getDomainObjects()
    {
        DomainObjectIf[] domainObjects = getCache().getDomainObjects();
        return domainObjects;
    }

    void addToCache(DomainObjectIf domainObject, DomainObjectId id)
    {
        getCache().put(id, domainObject);
    }

    void removeFromCache(DomainObjectId id)
    {
        getCache().remove(id);
    }

    DomainObjectIf get(DataSupplierIf dataSupplier)
    {
        assert dataSupplier != null;

        DomainObjectId objectId = (DomainObjectId) dataSupplier.getObjectId();
        DomainObjectIf domainObject = getCache().get(objectId);

        assert !dataSupplier.supportsPersistentData() ? domainObject != null : true;

        if (domainObject == null)
        {
            Class dataSupplierInterface = objectId.getDataSupplierInterfaceClass();

            s_Logger.debug("trying to create a domain object using data supplier interface '"
                            + dataSupplierInterface.getName() + "' instance of '" + dataSupplier.getClass().getName()
                            + "'");
            Constructor ctor = (Constructor) m_DataSupplierInterfaceClassToCtor.get(dataSupplierInterface);
            if (ctor == null)
            {
                s_Logger.error("no ctor registered for data supplier interface '" + dataSupplierInterface.getName()
                                + "'");
                assert false : "no ctor registered for data supplier interface " + dataSupplierInterface.getName();
            }

            try
            {
                s_CtorArgs[0] = dataSupplier;
                s_CtorArgs[1] = new DataSupplierReadMarker();
                domainObject = (DomainObjectIf) ctor.newInstance(s_CtorArgs);
            }
            catch (InstantiationException e)
            {
                s_Logger.error("domain object could not be created - " + ExceptionUtility.collectAll(e));
                assert false : "domain object could not be created - " + ExceptionUtility.collectAll(e);
            }
            catch (IllegalAccessException e)
            {
                s_Logger.error("domain object could not be created - " + ExceptionUtility.collectAll(e));
                assert false : "domain object could not be created - " + ExceptionUtility.collectAll(e);
            }
            catch (InvocationTargetException e)
            {
                s_Logger.error("domain object could not be created - " + ExceptionUtility.collectAll(e));
                assert false : "domain object could not be created - " + ExceptionUtility.collectAll(e);
            }
        }

        return domainObject;
    }
}