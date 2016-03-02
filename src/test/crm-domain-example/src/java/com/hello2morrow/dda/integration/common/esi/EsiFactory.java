package com.hello2morrow.dda.integration.common.esi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;

public final class EsiFactory
{
    private static Logger s_Logger = Logger.getLogger(EsiFactory.class);
    private static EsiFactory s_Instance = null;
    private final Map m_EsiToEsiImplementation = new HashMap();

    public static synchronized void createInstance(String properties) throws IOException
    {
        assert properties != null;
        assert properties.length() > 0;
        InputStream in = EsiFactory.class.getResourceAsStream(properties);
        if (in == null)
        {
            throw new MissingResourceException("properties file not found = " + properties, EsiFactory.class.getName(),
                            properties);
        }
        Properties loadedProperties = new Properties();
        loadedProperties.load(in);
        createInstance(loadedProperties);
    }

    public static synchronized void createInstance(Properties properties)
    {
        assert s_Instance == null : "factory has already been created";
        s_Instance = new EsiFactory(properties);
    }

    public static EsiFactory getInstance()
    {
        assert s_Instance != null : "factory has not been created";
        return s_Instance;
    }

    public static synchronized void deleteInstance()
    {
        assert s_Instance != null : "factory has not been created";
        s_Instance = null;
    }

    private EsiFactory(Properties properties)
    {
        assert properties != null;

        s_Logger.debug("processing properties ...");

        Enumeration enumKeys = properties.keys();
        while (enumKeys.hasMoreElements())
        {
            String key = (String) enumKeys.nextElement();
            String value = properties.getProperty(key);
            createEsiImplementation(key.trim(), value.trim());
        }

        s_Logger.debug("properties loaded");
    }

    public ExternalSystemIf getEsiImplementation(Class esiClass)
    {
        assert esiClass != null;

        ExternalSystemIf service = (ExternalSystemIf) m_EsiToEsiImplementation.get(esiClass);

        if (service == null)
        {
            s_Logger.debug("no esi implementation registered for esi '" + esiClass.getName() + "'");
            assert false : "no esi implementation registered for esi '" + esiClass.getName() + "'";
        }

        return service;
    }

    private void createEsiImplementation(String esi, String esiImpl)
    {
        assert esi != null;
        assert esi.length() > 0;
        assert esiImpl != null;
        assert esiImpl.length() > 0;

        try
        {
            Class serviceInterface = Class.forName(esi);
            Class serviceClass = Class.forName(esiImpl);

            ExternalSystemIf service = (ExternalSystemIf) m_EsiToEsiImplementation.get(serviceInterface);
            assert service == null : "duplicate esi key '" + esi + "'";
            service = (ExternalSystemIf) serviceClass.newInstance();
            m_EsiToEsiImplementation.put(serviceInterface, service);
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