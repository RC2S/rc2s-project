package com.rc2s.application.services.authentication;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class JDBCLoginModule implements LoginModule
{
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;
    
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
        Map<String, ?> sharedState, Map<String, ?> options)
    {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    @Override
    public boolean login() throws LoginException
    {
        return false;
    }

    @Override
    public boolean commit() throws LoginException
    {
        return false;
    }

    @Override
    public boolean abort() throws LoginException
    {
        return false;
    }

    @Override
    public boolean logout() throws LoginException
    {
        return false;
    }
}
