package com.rc2s.dao.user;

import com.rc2s.common.exceptions.DAOException;
import com.rc2s.common.vo.User;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class UserDAO implements IUserDAO
{  
    @PersistenceContext
    private EntityManager em;
    
	@Override
    public List<User> getUsers() throws DAOException
    {
		try 
		{
			Query query = em.createQuery("SELECT u from User as u");
			return query.getResultList();
		}
		catch(Exception e)
		{
			throw new DAOException(e);
		}
    }
	
	@Override
	public User getAuthenticatedUser(String username, String password) throws DAOException
	{
		try
		{
			Query query = em.createQuery("SELECT u from User as u WHERE u.username = :username AND u.password = :password AND u.activated = 1 AND u.locked = 0")
							.setParameter("username", username)
							.setParameter("password", password);
			return (User)query.getSingleResult();
		}
		catch(Exception e)
		{
			throw new DAOException(e);
		}
	}
	
	@Override
	public int setLastLogin(User user) throws DAOException
	{
		try
		{
			Query query = em.createQuery("UPDATE User SET lastLogin = :lastLogin WHERE id = :id")
							.setParameter("lastLogin", new Date())
							.setParameter("id", user.getId());
			return query.executeUpdate();
		}
		catch(Exception e)
		{
			throw new DAOException(e);
		}
	}
}