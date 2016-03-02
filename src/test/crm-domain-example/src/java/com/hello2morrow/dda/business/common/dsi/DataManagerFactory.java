package com.hello2morrow.dda.business.common.dsi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;

/**
 * Creates the manager implementations for the manager interfaces. One
 * implementation can serve n interfaces.
 */
public final class DataManagerFactory
{
    private static Logger s_Logger = Logger.getLogger(DataManagerFactory.class);
    private static DataManagerFactory s_Instance = null;
    private Map m_DataSupplierInterfaceClassToDataManagerImplementation = new HashMap();
    private Map m_ManagerImplementationClassNameToManagerImplementation = new HashMap();

    public static synchronized void createInstance(String properties) throws IOException
    {
        assert properties != null;
        assert properties.length() > 0;
        InputStream in = DataManagerFactory.class.getResourceAsStream(properties);
        if (in == null)
        {
            throw new MissingResourceException("properties file not found = " + properties,
                            DataManagerFactory.class.getName(), properties);
        }
        Properties loadedProperties = new Properties();
        loadedProperties.load(in);
        createInstance(loadedProperties);
    }

    public static synchronized void createInstance(Properties properties)
    {
        assert s_Instance == null : "data manager factory has already been created";
        s_Instance = new DataManagerFactory(properties);
        s_Logger.debug("dataManagerFactory created");
    }

    public static DataManagerFactory getInstance()
    {
        assert s_Instance != null : "data manager factory has not been created";
        return s_Instance;
    }

    public static synchronized void deleteInstance()
    {
        assert s_Instance != null : "data manager factory has not been created";
        s_Instance = null;
    }

    private DataManagerFactory(Properties properties)
    {
        assert properties != null;
        s_Logger.debug("processing properties ...");
        Enumeration enumKeys = properties.keys();
        while (enumKeys.hasMoreElements())
        {
            String key = (String) enumKeys.nextElement();
            String value = properties.getProperty(key);
            createDataManager(key.trim(), value.trim());
        }
    }

    public boolean hasDataManagerImplementation(Class dataSupplierInterface)
    {
        assert dataSupplierInterface != null;
        return m_DataSupplierInterfaceClassToDataManagerImplementation.containsKey(dataSupplierInterface);
    }

    public DataManagerIf getDataManagerImplementation(Class dataSupplierInterfaceClass)
    {
        assert dataSupplierInterfaceClass != null;
        assert DataSupplierIf.class.isAssignableFrom(dataSupplierInterfaceClass);

        DataManagerIf manager = (DataManagerIf) m_DataSupplierInterfaceClassToDataManagerImplementation.get(dataSupplierInterfaceClass);

        if (manager == null)
        {
            s_Logger.debug("do data manager implementation registered for data supplier interface '"
                            + dataSupplierInterfaceClass.getName() + "'");
            assert false : "no data manager implementation registered for data supplier interface '"
                            + dataSupplierInterfaceClass.getName() + "'";
        }

        return manager;
    }

    private void createDataManager(String dataSupplierInterfaceClassName, String dataManagerImplClassName)
    {
        assert dataSupplierInterfaceClassName != null;
        assert dataSupplierInterfaceClassName.length() > 0;
        assert dataManagerImplClassName != null;
        assert dataManagerImplClassName.length() > 0;

        s_Logger.debug("trying to create the data manager '" + dataManagerImplClassName + "' for '"
                        + dataSupplierInterfaceClassName + "'");

        try
        {
            Class dataSupplierInterfaceClass = Class.forName(dataSupplierInterfaceClassName);
            DataManagerIf dataManager = (DataManagerIf) m_ManagerImplementationClassNameToManagerImplementation.get(dataManagerImplClassName);

            if (dataManager == null)
            {
                assert !m_DataSupplierInterfaceClassToDataManagerImplementation.containsKey(dataSupplierInterfaceClass);
                Class persistentManagerImplClass = Class.forName(dataManagerImplClassName);
                dataManager = (DataManagerIf) persistentManagerImplClass.newInstance();
                registerDataManager(dataSupplierInterfaceClass, dataManager);
            }
            else
            {
                s_Logger.debug("data manager implementation already registered - reusing (data supplier interface class name/data manager implementation class name) = "
                                + dataSupplierInterfaceClassName + "/" + dataManagerImplClassName);

                m_DataSupplierInterfaceClassToDataManagerImplementation.put(dataSupplierInterfaceClass, dataManager);
            }
        }
        catch (ClassNotFoundException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false;
        }
        catch (InstantiationException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false;
        }
        catch (IllegalAccessException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false;
        }

    }

    private void registerDataManager(Class dataSupplierInterfaceClass, DataManagerIf managerImplementation)
    {
        assert dataSupplierInterfaceClass != null;
        assert DataSupplierIf.class.isAssignableFrom(dataSupplierInterfaceClass);
        assert managerImplementation != null;

        s_Logger.debug("data manager implementation created (Data supplier interface class name/Manager implementation class name) = "
                        + dataSupplierInterfaceClass.getName() + "/" + managerImplementation.getClass().getName());

        m_ManagerImplementationClassNameToManagerImplementation.put(managerImplementation.getClass().getName(),
                        managerImplementation);

        m_DataSupplierInterfaceClassToDataManagerImplementation.put(dataSupplierInterfaceClass, managerImplementation);
    }
}