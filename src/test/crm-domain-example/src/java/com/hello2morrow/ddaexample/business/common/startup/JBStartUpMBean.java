package com.hello2morrow.ddaexample.business.common.startup;

public interface JBStartUpMBean
{
    public void start() throws Exception;

    public void stop() throws Exception;

    public int getInt() throws Exception;
}