package com.hello2morrow.ddaexample.business.user.service;

import com.hello2morrow.dda.business.common.service.Dto;
import com.hello2morrow.dda.foundation.common.EncryptUtil;
import com.hello2morrow.dda.foundation.common.exception.TechnicalException;


public final class ChangePasswordDto extends Dto
{
    private transient String m_CurrentPwd;
    private String m_CurrentEncryptedPwd;
    private transient String m_NewPwd;
    private String m_NewEncryptedPwd;
    private transient String m_NewPwdRetyped;
    private String m_NewEncryptedPwdRetyped;

    public ChangePasswordDto(String currentPwd, String newPwd, String newPwdRetyped) throws TechnicalException
    {
        assert currentPwd != null;
        assert newPwd != null;
        assert newPwdRetyped != null;

        m_CurrentPwd = currentPwd;
        m_NewPwd = newPwd;
        m_NewPwdRetyped = newPwdRetyped;

        if (m_CurrentPwd.length() > 0)
        {
            m_CurrentEncryptedPwd = EncryptUtil.encrypt(m_CurrentPwd);
        }
        else
        {
            m_CurrentEncryptedPwd = null;
        }
        if (m_NewPwd.length() > 0)
        {
            m_NewEncryptedPwd = EncryptUtil.encrypt(m_NewPwd);
        }
        else
        {
            m_NewEncryptedPwd = null;
        }
        if (m_NewPwdRetyped.length() > 0)
        {
            m_NewEncryptedPwdRetyped = EncryptUtil.encrypt(m_NewPwdRetyped);
        }
        else
        {
            m_NewEncryptedPwdRetyped = null;
        }
    }

    public String getCurrentPwd()
    {
        return m_CurrentPwd;
    }

    public String getNewEncryptedPwd()
    {
        return m_NewEncryptedPwd;
    }

    public String getNewEncryptedPwdRetyped()
    {
        return m_NewEncryptedPwdRetyped;
    }

    public String getNewPwd()
    {
        return m_NewPwd;
    }

    public String getNewPwdRepeated()
    {
        return m_NewPwdRetyped;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        return buffer.toString();
    }

    public String getCurrentEncryptedPwd()
    {
        return m_CurrentEncryptedPwd;
    }
}