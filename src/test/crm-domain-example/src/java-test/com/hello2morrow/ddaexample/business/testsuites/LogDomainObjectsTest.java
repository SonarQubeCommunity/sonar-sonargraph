package com.hello2morrow.ddaexample.business.testsuites;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.dsi.DomainObjectFactory;
import com.hello2morrow.dda.business.common.dsi.DomainObjectIf;

public abstract class LogDomainObjectsTest extends TestCase
{
    private static Logger s_Logger = Logger.getLogger(LogDomainObjectsTest.class);

    public void testLogDomainObjects()
    {
        DomainObjectIf[] all = DomainObjectFactory.getInstance().getDomainObjects();
        s_Logger.info("### Start Domain Object Dump (" + all.length + ")");
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.info(all[i]);
        }
    }
}