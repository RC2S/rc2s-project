package com.rc2s.application.services.synchronization;

import com.rc2s.common.exceptions.ServiceException;
import com.rc2s.common.vo.Synchronization;
import com.rc2s.common.vo.User;

import java.util.List;
import javax.ejb.Local;

/**
 * ISynchronizationService interface
 * 
 * Service interface for synchronisations management
 * 
 * @author RC2S
 */
@Local
public interface ISynchronizationService
{
	public List<Synchronization> getAll() throws ServiceException;
    
	public List<Synchronization> getByUser(final User user) throws ServiceException;
    
	public void add(final Synchronization synchronization) throws ServiceException;
}
