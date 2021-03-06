package com.rc2s.ejb.size;

import com.rc2s.common.exceptions.EJBException;
import com.rc2s.common.vo.Size;
import java.util.List;
import javax.ejb.Remote;

/**
 * SizeFacadeRemote
 * 
 * EJB remote interface for Size EJB
 * 
 * @author RC2S
 */
@Remote
public interface SizeFacadeRemote
{
	public List<Size> getAll() throws EJBException;
    
	public Size add(final Size size) throws EJBException;
}
