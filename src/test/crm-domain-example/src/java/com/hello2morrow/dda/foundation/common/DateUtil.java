package com.hello2morrow.dda.foundation.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.hello2morrow.dda.foundation.common.exception.BusinessException;

public final class DateUtil
{
    private static DateFormat s_DateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    private DateUtil()
    {
        //Just to make it unaccesible
    }

    public static Date convert(String date) throws BusinessException
    {
        if (date == null || date.length() == 0)
        {
            return null;
        }

        Date created = null;

        try
        {
            created = s_DateFormat.parse(date);
        }
        catch (ParseException e)
        {
            throw new BusinessException("'" + date + "' besitzt kein unterstütztes Datumsformat", e);
        }

        return created;
    }

    public static String convert(Date date)
    {
        if (date == null)
        {
            return null;
        }
        return s_DateFormat.format(date);
    }

    public static Date getDate()
    {
        return Calendar.getInstance().getTime();
    }

    public static String getTimestamp()
    {
        return s_DateFormat.format(Calendar.getInstance().getTime());
    }
}