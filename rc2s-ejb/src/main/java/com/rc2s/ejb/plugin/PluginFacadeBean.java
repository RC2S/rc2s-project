package com.rc2s.ejb.plugin;

import com.rc2s.application.services.plugin.IPluginService;
import com.rc2s.common.exceptions.EJBException;
import com.rc2s.common.exceptions.ServiceException;
import com.rc2s.common.vo.Plugin;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.logging.log4j.Logger;

/**
 * PluginFacadeBean
 * 
 * Plugin EJB, bridge to PluginService
 * 
 * @author RC2S
 */
@Stateless(mappedName = "PluginEJB")
public class PluginFacadeBean implements PluginFacadeRemote
{
	@EJB
	private IPluginService pluginService;
    
    @Inject
    private Logger log;

	/**
	 * getAll
	 * 
	 * Get all the plugins in db
	 * 
	 * @return
	 * @throws EJBException 
	 */
	@Override
    @RolesAllowed({"admin"})
	public List<Plugin> getAll() throws EJBException
	{
		try
		{
			return pluginService.getAll();
		}
		catch(ServiceException e)
		{
            log.error(e);
			throw new EJBException(e);
		}
	}

	/**
	 * getAvailables
	 * 
	 * Get available plugins in db
	 * 
	 * @return
	 * @throws EJBException 
	 */
	@Override
    @RolesAllowed({"user"})
	public List<Plugin> getAvailables() throws EJBException
	{
		try
		{
			return pluginService.getAvailables();
		}
		catch(ServiceException e)
		{
            log.error(e);
			throw new EJBException(e);
		}
	}

	/**
	 * add
	 * 
	 * Add a given plugin to db
	 * 
	 * @param plugin
	 * @return Plugin added
	 * @throws EJBException 
	 */
	@Override
    @RolesAllowed({"admin"})
	public Plugin add(final Plugin plugin) throws EJBException
	{
		try
		{
			return pluginService.add(plugin);
		}
		catch(ServiceException e)
		{
            log.error(e);
			throw new EJBException(e);
		}
	}

	/**
	 * update
	 * 
	 * Update a given plugin in db
	 * 
	 * @param plugin
	 * @return Plugin updated
	 * @throws EJBException 
	 */
	@Override
    @RolesAllowed({"admin"})
	public Plugin update(final Plugin plugin) throws EJBException
	{
		try
		{
			return pluginService.update(plugin);
		}
		catch(ServiceException e)
		{
            log.error(e);
			throw new EJBException(e);
		}
	}

	/**
	 * delete
	 * 
	 * Delete a given plugin in db
	 * 
	 * @param plugin
	 * @throws EJBException 
	 */
	@Override
    @RolesAllowed({"admin"})
	public void delete(final Plugin plugin) throws EJBException
	{
		try
		{
			pluginService.delete(plugin);
		}
		catch(ServiceException e)
		{
            log.error(e);
			throw new EJBException(e);
		}
	}
}
