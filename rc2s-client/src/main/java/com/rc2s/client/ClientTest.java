package com.rc2s.client;

import com.rc2s.common.exceptions.EJBException;
import com.rc2s.common.utils.EJB;
import com.rc2s.common.vo.User;
import com.rc2s.ejb.user.UserFacadeRemote;
import java.util.List;
import javax.naming.NamingException;

public class ClientTest
{
    public static void main(String[] args)
    {
        try
        {
            // Test UsersEJB
            System.out.println("Client Context" + EJB.getContext());
            UserFacadeRemote userEJB = (UserFacadeRemote)EJB.getContext().lookup("UserEJB");
			
			try
			{
				List<User> users = userEJB.getAll();
				for(User user : users) {
					System.out.println(user.getId() + " " + user.getUsername() 
						+ " " + user.getPassword() + " " + user.getCreated());
				}
			}
			catch(EJBException e)
			{
				e.printStackTrace();
			}
        }
        catch(NamingException e)
        {
            e.printStackTrace();
        }
    }
}
