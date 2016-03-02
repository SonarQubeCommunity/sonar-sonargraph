package com.hello2morrow.ddaexample.business.user.service;

import java.util.ArrayList;
import java.util.List;

import com.hello2morrow.dda.business.common.service.Dto;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;


public final class ContextDto extends Dto
{
    private final ObjectIdIf m_UserRef;
    private final String m_UserName;
    private List m_RoleNames = new ArrayList();
    private List m_RoleReferences = new ArrayList();

    public ContextDto(ObjectIdIf userRef, String userName)
    {
        assert userRef != null;
        assert userName != null;
        assert userName.length() > 0;
        m_UserRef = userRef;
        m_UserName = userName;
    }

    public void addRole(String roleName, ObjectIdIf role)
    {
        assert roleName != null;
        assert roleName.length() > 0;
        assert role != null;
        assert !m_RoleNames.contains(roleName);
        assert !m_RoleReferences.contains(role);
        m_RoleNames.add(roleName);
        m_RoleReferences.add(role);
    }

    public String[] getRoles()
    {
        return (String[]) m_RoleNames.toArray(new String[0]);
    }

    public String getUserName()
    {
        return m_UserName;
    }

    public ObjectIdIf[] getRoleReferences()
    {
        return (ObjectIdIf[]) m_RoleReferences.toArray(new ObjectIdIf[0]);
    }

    public ObjectIdIf getUserReference()
    {
        return m_UserRef;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);
        buffer.append("user = ");
        buffer.append(getUserName());
        buffer.append(LINE_SEPARATOR);
        String[] roles = getRoles();
        for (int i = 0; i < roles.length; i++)
        {
            buffer.append("has role = ");
            buffer.append(roles[i]);
            buffer.append(LINE_SEPARATOR);
        }
        return buffer.toString();
    }
}