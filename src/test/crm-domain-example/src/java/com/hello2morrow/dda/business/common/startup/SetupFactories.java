package com.hello2morrow.dda.business.common.startup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.hello2morrow.dda.business.common.controller.DtoManager;
import com.hello2morrow.dda.business.common.dsi.DataManagerFactory;
import com.hello2morrow.dda.business.common.dsi.DomainObjectFactory;
import com.hello2morrow.dda.business.common.service.Dto;
import com.hello2morrow.dda.business.common.service.ServiceFactory;
import com.hello2morrow.dda.integration.common.esi.EsiFactory;

public final class SetupFactories
{
    private final static String DTO_VALIDATION_PROPERTIES = "/dto-validation.properties";
    private final static String DOMAIN_OBJECT_FACTORY_PROPERTIES = "/domain-object-factory.properties";
    private final static String DATA_MANAGER_FACTORY_PROPERTIES = "/data-manager-factory.properties";
    private final static String SERVICE_FACTORY_PROPERTIES = "/service-factory.properties";
    private final static String ESI_FACTORY_PROPERTIES = "/esi-factory.properties";

    private static boolean s_Initialized;

    private SetupFactories()
    {
        //make it unaccessible
    }

    public static synchronized void initialize()
    {
        assert !s_Initialized;

        Properties dtoValidationProperties = new Properties();
        Properties domainObjectFactoryProperties = new Properties();
        Properties dataManagerFactoryProperties = new Properties();
        Properties serviceFactoryProperties = new Properties();
        Properties esiFactoryProperties = new Properties();

        try
        {
            InputStream in = SetupFactories.class.getResourceAsStream(DTO_VALIDATION_PROPERTIES);
            assert in != null : DTO_VALIDATION_PROPERTIES + " could not be found";
            dtoValidationProperties.load(in);
            in = SetupFactories.class.getResourceAsStream(DOMAIN_OBJECT_FACTORY_PROPERTIES);
            assert in != null : DOMAIN_OBJECT_FACTORY_PROPERTIES + " could not be found";
            domainObjectFactoryProperties.load(in);
            in = SetupFactories.class.getResourceAsStream(DATA_MANAGER_FACTORY_PROPERTIES);
            assert in != null : DATA_MANAGER_FACTORY_PROPERTIES + " could not be found";
            dataManagerFactoryProperties.load(in);
            in = SetupFactories.class.getResourceAsStream(SERVICE_FACTORY_PROPERTIES);
            assert in != null : SERVICE_FACTORY_PROPERTIES + " could not be found";
            serviceFactoryProperties.load(in);
            in = SetupFactories.class.getResourceAsStream(ESI_FACTORY_PROPERTIES);
            assert in != null : ESI_FACTORY_PROPERTIES + " could not be found";
            esiFactoryProperties.load(in);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        DtoManager.createInstance();
        Dto.initialize(dtoValidationProperties);
        DomainObjectFactory.createInstance(domainObjectFactoryProperties);
        DataManagerFactory.createInstance(dataManagerFactoryProperties);
        ServiceFactory.createInstance(serviceFactoryProperties);
        EsiFactory.createInstance(esiFactoryProperties);

        s_Initialized = true;
    }

    public static void cleanUp()
    {
        DtoManager.deleteInstance();
        Dto.cleanUp();
        DomainObjectFactory.deleteInstance();
        DataManagerFactory.deleteInstance();
        ServiceFactory.deleteInstance();
        EsiFactory.deleteInstance();
    }
}