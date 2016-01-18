package com.hello2morrow.dda.business.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.foundation.common.exception.DtoValidationException;

class DtoValidatorKey
{
    private final Class m_DtoClass;
    private final Class m_BusinessComponentClass;
    private final int m_HashCode;

    DtoValidatorKey(Class dtoClass, Class serviceClass)
    {
        assert dtoClass != null;
        assert serviceClass != null;
        m_DtoClass = dtoClass;
        m_BusinessComponentClass = serviceClass;
        String hcBase = m_DtoClass.getName() + m_BusinessComponentClass.getName();
        m_HashCode = hcBase.hashCode();
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj == this)
        {
            return true;
        }
        else if (obj instanceof DtoValidatorKey)
        {
            if (m_DtoClass == ((DtoValidatorKey) obj).m_DtoClass
                            && m_BusinessComponentClass == ((DtoValidatorKey) obj).m_BusinessComponentClass)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return m_HashCode;
    }
}

public abstract class Dto implements Serializable, DtoIf
{
    protected final static String LINE_SEPARATOR = System.getProperty("line.separator");
    private static boolean s_PropertiesLoaded;
    private static Logger s_Logger = Logger.getLogger(Dto.class);
    private static Map s_DtoClassToDtoValidator = new HashMap();

    public static synchronized void initialize(Properties properties)
    {
        processProperties(properties);
    }

    public static synchronized void initialize(String properties) throws IOException
    {
        assert properties != null;
        assert properties.length() > 0;
        InputStream in = Dto.class.getResourceAsStream(properties);
        if (in == null)
        {
            throw new MissingResourceException("properties file not found = " + properties, Dto.class.getName(),
                            properties);
        }
        Properties loadedProperties = new Properties();
        loadedProperties.load(in);
        processProperties(loadedProperties);
    }

    private static void processProperties(Properties properties)
    {
        assert !s_PropertiesLoaded;
        assert properties != null;

        s_Logger.debug("processing properties ...");
        Enumeration enumKeys = properties.keys();
        while (enumKeys.hasMoreElements())
        {
            String key = (String) enumKeys.nextElement();
            String value = properties.getProperty(key);
            createDtoValidator(key.trim(), value.trim());
        }

        s_Logger.debug("properties loaded");
        s_PropertiesLoaded = true;
    }

    private static DtoValidatorKey createKeyFromValue(String value) throws ClassNotFoundException
    {
        assert value != null;
        assert value.length() > 0;

        DtoValidatorKey key = null;

        int pos = value.indexOf(',');
        assert pos != -1 : "check syntax (dto class, service class ) in '" + value + "'";
        String className = value.substring(0, pos);
        Class dtoClass = Class.forName(className.trim());
        className = value.substring(pos + 1);
        Class serviceClass = Class.forName(className.trim());
        key = new DtoValidatorKey(dtoClass, serviceClass);

        return key;
    }

    private static void createDtoValidator(String key, String value)
    {
        assert key != null;
        assert key.length() > 0;

        try
        {
            Class validatorClass = Class.forName(key);
            DtoValidatorIf dtoValidator = (DtoValidatorIf) validatorClass.newInstance();
            DtoValidatorKey dtoValKey = createKeyFromValue(value);
            assert !s_DtoClassToDtoValidator.containsKey(dtoValKey) : "duplicate key '" + dtoValKey + "' in properties";
            s_DtoClassToDtoValidator.put(dtoValKey, dtoValidator);
        }
        catch (SecurityException exc)
        {
            assert false : exc.toString();
        }
        catch (ClassNotFoundException exc)
        {
            assert false : exc.toString();
        }
        catch (InstantiationException exc)
        {
            assert false : exc.toString();
        }
        catch (IllegalAccessException exc)
        {
            assert false : exc.toString();
        }
    }

    public final void validate(Class serviceClass, String serverCmdId) throws DtoValidationException
    {
        assert serviceClass != null;
        assert serverCmdId != null;
        assert serverCmdId.length() > 0;
        Class dtoClass = getClass();
        DtoValidatorKey key = new DtoValidatorKey(dtoClass, serviceClass);
        DtoValidatorIf dtoValidator = (DtoValidatorIf) s_DtoClassToDtoValidator.get(key);
        if (dtoValidator != null)
        {
            synchronized (this)
            {
                dtoValidator.clearErrors();
                dtoValidator.validate(serverCmdId, this);
            }
        }
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(getClass().getName());
        return buffer.toString();
    }

    public static void cleanUp()
    {
        s_DtoClassToDtoValidator.clear();
        s_PropertiesLoaded = false;
    }
}