package com.hello2morrow.dda.foundation.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

import com.hello2morrow.dda.foundation.common.exception.TechnicalException;

public final class EncryptUtil
{
    private static Logger s_Logger = Logger.getLogger(EncryptUtil.class);

    public static void main(String[] args)
    {
        assert args != null;
        if (args.length != 1)
        {
            s_Logger.error("need exactly one argument");
        }
        else
        {
            try
            {
                s_Logger.info("Encrypted argument = " + EncryptUtil.encrypt(args[0]));
            }
            catch (TechnicalException e)
            {
                e.printStackTrace();
            }
        }
    }

    private EncryptUtil()
    {
        //Just to make the ctor unaccessible
    }

    public static String encrypt(String plaintext) throws TechnicalException
    {
        assert plaintext != null;
        assert plaintext.length() > 0;

        MessageDigest md = null;

        try
        {
            md = MessageDigest.getInstance("SHA");
            md.update(plaintext.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new TechnicalException(e);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new TechnicalException(e);
        }

        byte raw[] = md.digest();
        String hash = (new BASE64Encoder()).encode(raw);
        return hash;
    }
}