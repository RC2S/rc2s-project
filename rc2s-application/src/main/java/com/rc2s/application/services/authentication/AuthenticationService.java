package com.rc2s.application.services.authentication;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

@Dependent
public class AuthenticationService implements IAuthenticationService
{
    @Override
    public boolean authentify()
    {
        try {
            LoginContext lc = new LoginContext("JDBCLoginModule", new JDBCCallbackHandler(null, null));
            
            lc.login();
            
            Subject subject = lc.getSubject();
            
        } catch (LoginException ex) {
            Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE,
                null, ex);
        }
        return false;
    }
}
