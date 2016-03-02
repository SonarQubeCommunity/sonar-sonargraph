package com.hello2morrow.ddaexample.business.user.service;

import com.hello2morrow.dda.business.common.service.Dto;

public final class LoginDto extends Dto
{
    private final String m_UserName;
    private final String m_EncryptedPwd;

    public LoginDto(String name, String encryptedPwd)
    {
        assert name != null;
        assert encryptedPwd != null;
        m_UserName = name;
        m_EncryptedPwd = encryptedPwd;
    }

    public String getUserName()
    {
        return m_UserName;
    }

    public String getEncryptedPwd()
    {
        return m_EncryptedPwd;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);
        buffer.append("user name = ");
        buffer.append(m_UserName);
        buffer.append(LINE_SEPARATOR);
        buffer.append("encrypted pwd = ");
        buffer.append(m_EncryptedPwd);
        return buffer.toString();
    }
}