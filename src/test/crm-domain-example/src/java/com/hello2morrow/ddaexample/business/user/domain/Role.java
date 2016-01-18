package com.hello2morrow.ddaexample.business.user.domain;

import org.apache.log4j.Logger;

import com.hello2morrow.dda.business.common.dsi.DataSupplierReadMarker;
import com.hello2morrow.dda.business.common.dsi.DomainObjectWithDataSupplier;
import com.hello2morrow.dda.foundation.common.exception.AssertionUtility;
import com.hello2morrow.ddaexample.business.user.dsi.RoleDmi;
import com.hello2morrow.ddaexample.business.user.dsi.RoleDsi;
import com.hello2morrow.ddaexample.business.user.dsi.ServerCommandDsi;

/**
 * @dda-generate-cmp
 */
public final class Role extends DomainObjectWithDataSupplier
{
    private static Logger s_Logger = Logger.getLogger(Role.class);

    /**
     * @dda-dmi-find
     */
    public static Role[] findAllRoles()
    {
        RoleDmi dmi = (RoleDmi) getDataManager(RoleDsi.class);
        RoleDsi[] all = dmi.findAllRoles();
        return (Role[]) getDomainObjects(all, Role.class);
    }

    /**
     * @dda-dmi-find
     */
    public static Role findRoleByName(String name)
    {
        assert name != null;
        assert name.length() > 0;
        RoleDmi dmi = (RoleDmi) getDataManager(RoleDsi.class);
        RoleDsi dsi = dmi.findRoleByName(name);
        return (Role) getDomainObject(dsi);
    }

    /**
     * required for creation direct from data source 
     */
    public Role(RoleDsi dsi, DataSupplierReadMarker marker)
    {
        super(dsi, marker);
    }

    /**
     * creates a persistent role
     */
    public Role(String name)
    {
        super(getDataManager(RoleDsi.class).createDataSupplier(RoleDsi.class, true));
        setName(name);
    }

    /**
     * @dda-dto
     * @dda-dsi
     */
    public String getName()
    {
        return ((RoleDsi) getDataSupplier()).getName();
    }

    private void setName(String name)
    {
        assert name != null;
        assert name.length() > 0;
        ((RoleDsi) getDataSupplier()).setName(name);
    }

    /**
     * @dda-dto
     * @dda-dsi n-m no-duplicates
     */
    public ServerCommand[] getServerCommands()
    {
        ServerCommandDsi[] cmds = ((RoleDsi) getDataSupplier()).getServerCommands();
        return (ServerCommand[]) getDomainObjects(cmds, ServerCommand.class);
    }

    public void setServerCommands(ServerCommand[] serverCommands)
    {
        assert AssertionUtility.checkArray(serverCommands);
        ((RoleDsi) getDataSupplier()).setServerCommands((ServerCommandDsi[]) getDataSuppliers(serverCommands,
                        ServerCommandDsi.class));
    }

    boolean hasServerCommand(ServerCommand serverCommand)
    {
        assert serverCommand != null;
        ServerCommand[] all = getServerCommands();
        for (int i = 0; i < all.length; i++)
        {
            s_Logger.debug("role has server-command : " + all[i].getName());
            if (serverCommand.equals(all[i]))
            {
                return true;
            }
        }

        return false;
    }
}