package com.rc2s.dao.cube;

import com.rc2s.common.exceptions.DAOException;
import com.rc2s.common.vo.Cube;
import com.rc2s.common.vo.User;
import com.rc2s.dao.IGenericDAO;
import java.util.List;
import javax.ejb.Local;

/**
 * ICubeDAO
 * 
 * Interface for cubes management
 * 
 * @author RC2S
 */
@Local
public interface ICubeDAO extends IGenericDAO<Cube>
{
	public List<Cube> getCubes(final User user) throws DAOException;
    
	public Cube getByIp(final String ipAddress) throws DAOException;
}
