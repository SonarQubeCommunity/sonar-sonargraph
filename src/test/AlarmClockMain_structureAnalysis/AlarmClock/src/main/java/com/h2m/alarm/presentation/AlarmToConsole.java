package com.h2m.alarm.presentation;

public final class AlarmToConsole extends AlarmHandler
{
    @Override
    public void handleAlarm()
    {
        System.out.println("Alarm received");
    }
}