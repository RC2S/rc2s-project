package com.rc2s.application.services.role;

import com.rc2s.common.exceptions.DAOException;
import com.rc2s.common.exceptions.ServiceException;
import com.rc2s.common.vo.Role;
import com.rc2s.dao.role.IRoleDAO;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class RoleService implements IRoleService
{
	@EJB
	private IRoleDAO roleDAO;
	
	@Override
	public List<Role> getAll() throws ServiceException
	{
		try
		{
			return roleDAO.getAll();
		}
		catch(DAOException e)
		{
			throw new ServiceException(e);
		}
	}
	
}
