package com.hello2morrow.ddaexample.business.common.startup;

import com.hello2morrow.dda.business.common.startup.SetupFactories;

public class JBStartUpMBeanImpl implements JBStartUpMBean
{
    public void start() throws Exception
    {
        SetupFactories.initialize();
    }

    public void stop() throws Exception
    {
        SetupFactories.cleanUp();
    }

    public int getInt()  throws Exception
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
