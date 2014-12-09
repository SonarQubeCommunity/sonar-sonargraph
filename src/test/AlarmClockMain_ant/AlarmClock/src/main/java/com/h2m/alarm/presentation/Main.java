package com.h2m.alarm.presentation;

import com.h2m.alarm.model.AlarmClock;

public final class Main
{
    public static void main(String[] args)
    {
        AlarmToConsole alarmToConsole = new AlarmToConsole();
        AlarmToFile alarmToFile = new AlarmToFile();
        AlarmClock alarmClock = new AlarmClock();
        alarmClock.addObserver(alarmToConsole, AlarmClock.ALARM_EVENT);
        alarmClock.addObserver(alarmToFile, AlarmClock.ALARM_EVENT);
        new Thread(alarmClock).run();
    }
    
    public static void dummyMethodForCodeDuplication() {
    	int i = 0;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	i++;
    	
    	
    }
}