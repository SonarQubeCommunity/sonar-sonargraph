package com.hello2morrow.dda.business.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;

public final class ServiceFactory
{
    private static Logger s_Logger = Logger.getLogger(ServiceFactory.class);
    private static ServiceFactory s_Instance = null;
    private final Map m_ServiceToImplementation = new HashMap();

    public static synchronized void createInstance(String properties) throws IOException
    {
        assert properties != null;
        assert properties.length() > 0;
        InputStream in = ServiceFactory.class.getResourceAsStream(properties);
        if (in == null)
        {
            throw new MissingResourceException("properties file not found = " + properties,
                            ServiceFactory.class.getName(), properties);
        }
        Properties loadedProperties = new Properties();
        loadedProperties.load(in);
        createInstance(loadedProperties);
    }

    public static synchronized void createInstance(Properties properties)
    {
        assert s_Instance == null : "factory has already been created";
        s_Instance = new ServiceFactory(properties);
    }

    public static ServiceFactory getInstance()
    {
        assert s_Instance != null : "factory has not been created";
        return s_Instance;
    }

    public static synchronized void deleteInstance()
    {
        assert s_Instance != null : "factory has not been created";
        s_Instance = null;
    }

    private ServiceFactory(Properties properties)
    {
        assert properties != null;

        s_Logger.debug("processing properties ...");

        Enumeration enumKeys = properties.keys();
        while (enumKeys.hasMoreElements())
        {
            String key = (String) enumKeys.nextElement();
            String value = properties.getProperty(key);
            createServiceImplementation(key.trim(), value.trim());
        }

        s_Logger.debug("properties loaded");
    }

    public ServiceIf getService(Class serviceClass)
    {
        assert serviceClass != null;

        ServiceIf service = (ServiceIf) m_ServiceToImplementation.get(serviceClass);

        if (service == null)
        {
            s_Logger.debug("no service implementation registered for service '" + serviceClass.getName() + "'");
            assert false : "no service implementation registered for service '" + serviceClass.getName() + "'";
        }

        return service;
    }

    private void createServiceImplementation(String serviceClassName, String serviceImpl)
    {
        assert serviceClassName != null;
        assert serviceClassName.length() > 0;
        assert serviceImpl != null;
        assert serviceImpl.length() > 0;

        try
        {
            Class serviceInterface = Class.forName(serviceClassName);
            Class serviceClass = Class.forName(serviceImpl);

            ServiceIf service = (ServiceIf) m_ServiceToImplementation.get(serviceInterface);
            assert service == null : "duplicate service key '" + service + "'";
            service = (ServiceIf) serviceClass.newInstance();
            m_ServiceToImplementation.put(serviceInterface, service);
        }
        catch (ClassNotFoundException e)
        {
            s_Logger.debug(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
        catch (InstantiationException e)
        {
            s_Logger.debug(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
        catch (IllegalAccessException e)
        {
            s_Logger.debug(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
    }
}