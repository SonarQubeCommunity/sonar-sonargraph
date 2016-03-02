package com.hello2morrow.dda.business.common.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.dsi.DomainObject;
import com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier;
import com.hello2morrow.dda.business.common.service.Dto;
import com.hello2morrow.dda.foundation.common.exception.ExceptionUtility;

/**
 * Creates data transfer objects for domain objects.
 * Enables transparent creation of data transfer objects from clients
 * considering the type hierarchy. Provides the invocation start point
 * of mapping functionality.
 */
public final class DtoManager
{
    private static Logger s_Logger = Logger.getLogger(DtoManager.class);
    private static DtoManager s_Instance = null;
    private static Object[] s_NoArgCtor = new Object[0];
    private final Map m_DomainClassToDtoCtor = new HashMap();
    private final Map m_DomainClassToDomainObjectToDtoMapperMethod = new HashMap();

    public static synchronized void createInstance()
    {
        assert s_Instance == null : "dto manager has already been created";
        s_Instance = new DtoManager();
        s_Logger.debug("created");
    }

    public static DtoManager getInstance()
    {
        assert s_Instance != null : "dto manager has not been created";
        return s_Instance;
    }

    public static synchronized void deleteInstance()
    {
        assert s_Instance != null : "dto manager has not been created";
        s_Instance = null;
    }

    private DtoManager()
    {
        //make it unaccessible
    }

    public void addDtoCtor(Class domainClass, Constructor ctor)
    {
        assert domainClass != null;
        assert DomainObjectWithDataSupplier.class.isAssignableFrom(domainClass);
        assert ctor != null;
        assert Dto.class.isAssignableFrom(ctor.getDeclaringClass());
        assert !m_DomainClassToDtoCtor.containsKey(domainClass);

        m_DomainClassToDtoCtor.put(domainClass, ctor);
    }

    public void addDomainObjectToDtoMapper(Class domainClass, Method mapperMethod)
    {
        assert domainClass != null;
        assert DomainObject.class.isAssignableFrom(domainClass);
        assert mapperMethod != null;
        assert !m_DomainClassToDomainObjectToDtoMapperMethod.containsKey(domainClass);

        m_DomainClassToDomainObjectToDtoMapperMethod.put(domainClass, mapperMethod);
    }

    public Dto createDto(Class domainClass)
    {
        assert domainClass != null;
        assert DomainObject.class.isAssignableFrom(domainClass);
        assert m_DomainClassToDtoCtor.containsKey(domainClass);

        try
        {
            Constructor ctor = (Constructor) m_DomainClassToDtoCtor.get(domainClass);
            Dto created = (Dto) ctor.newInstance(s_NoArgCtor);
            return created;
        }
        catch (IllegalArgumentException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
        catch (InstantiationException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
        catch (IllegalAccessException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
        catch (InvocationTargetException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }

        return null;
    }

    public void mapDomainObjectToDto(DomainObject domainObject, Dto dto)
    {
        assert domainObject != null;
        assert m_DomainClassToDomainObjectToDtoMapperMethod.containsKey(domainObject.getClass());

        try
        {
            Method mapperMethod = (Method) m_DomainClassToDomainObjectToDtoMapperMethod.get(domainObject.getClass());
            mapperMethod.invoke(null, new Object[] { domainObject, dto});
        }
        catch (IllegalArgumentException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
        catch (IllegalAccessException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
        catch (InvocationTargetException e)
        {
            s_Logger.error(ExceptionUtility.collectAll(e));
            assert false : ExceptionUtility.collectAll(e);
        }
    }
}