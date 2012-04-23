package com.h2m.alarm.presentation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public final class AlarmToFile extends AlarmHandler
{
    @Override
    public void handleAlarm()
    {
        try
        {
            File file = new File("alarm.txt");
            file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            writer.println("Alarm received");
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
