package com.hello2morrow.ddaexample.business.user.service;

import java.util.HashMap;
import java.util.Map;

import com.hello2morrow.dda.business.common.service.Dto;
import com.hello2morrow.dda.foundation.common.ObjectIdIf;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;


public final class UserListDto extends Dto
{
    private Map m_UserNameToEntry = new HashMap();

    static final class Entry extends Dto
    {
        private ObjectIdIf m_UserRef;
        private String[] m_RoleNames;
        private ObjectIdIf[] m_RoleRefs;

        Entry(ObjectIdIf userRef, String[] roleNames, ObjectIdIf[] roleRefs)
        {
            assert userRef != null;
            assert AssertionUtility.checkArray(roleNames);
            assert AssertionUtility.checkArray(roleRefs);
            assert roleNames.length > 0;
            assert roleNames.length == roleRefs.length;
            m_UserRef = userRef;
            m_RoleNames = roleNames;
            m_RoleRefs = roleRefs;
        }

        public String[] getRoleNames()
        {
            return m_RoleNames;
        }

        public ObjectIdIf getObjectId()
        {
            return m_UserRef;
        }
    }

    public void addUser(String userName, ObjectIdIf userRef, String[] roleNames, ObjectIdIf[] roleRefs)
    {
        assert userName != null;
        assert userName.length() > 0;
        assert !m_UserNameToEntry.containsKey(userName);
        m_UserNameToEntry.put(userName, new Entry(userRef, roleNames, roleRefs));
    }

    public String[] getUserNames()
    {
        return (String[]) m_UserNameToEntry.keySet().toArray(new String[0]);
    }

    public ObjectIdIf getObjectIdForUser(String userName)
    {
        assert userName != null;
        assert m_UserNameToEntry.containsKey(userName);
        Entry userEntry = (Entry) m_UserNameToEntry.get(userName);
        return userEntry.getObjectId();
    }

    public String[] getRolesForUser(String userName)
    {
        assert userName != null;
        assert m_UserNameToEntry.containsKey(userName);
        Entry userEntry = (Entry) m_UserNameToEntry.get(userName);
        return userEntry.getRoleNames();
    }

    public int getNumberOfUsers()
    {
        return m_UserNameToEntry.size();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(LINE_SEPARATOR);
        String[] userNames = getUserNames();
        for (int i = 0; i < userNames.length; i++)
        {
            buffer.append("user = ");
            buffer.append(userNames[i]);
            buffer.append(LINE_SEPARATOR);
            String[] roles = getRolesForUser(userNames[i]);
            for (int j = 0; j < roles.length; j++)
            {
                buffer.append("has role = ");
                buffer.append(roles[j]);
                buffer.append(LINE_SEPARATOR);
            }
        }
        return buffer.toString();
    }
}