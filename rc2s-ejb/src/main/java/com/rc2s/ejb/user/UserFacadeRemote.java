package com.rc2s.ejb.user;

import com.rc2s.common.exceptions.EJBException;
import com.rc2s.common.vo.User;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface UserFacadeRemote
{
    public List<User> getAll() throws EJBException;
	public User login(String username, String password) throws EJBException;
	public User add(User user) throws EJBException;
	public User update(User user, boolean passwordUpdated) throws EJBException;
	public void delete(User user) throws EJBException;
}