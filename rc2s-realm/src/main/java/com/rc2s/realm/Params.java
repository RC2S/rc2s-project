package com.rc2s.realm;

/**
 * Params
 * 
 * Realm parameters configuration
 * 
 * @author RC2S
 */
public class Params
{
    public static final String DATASOURCE_JNDI  = "datasource-jndi";
	public static final String DB_USER          = "db-user";
	public static final String DB_PASSWORD      = "db-password";

	public static final String CHARSET						= "charset";
	public static final String USER_TABLE					= "user-table";
	public static final String USER_NAME_COLUMN				= "user-name-column";
	public static final String PASSWORD_COLUMN				= "password-column";
	public static final String GROUP_TABLE					= "group-table";
	public static final String GROUP_NAME_COLUMN			= "group-name-column";
	public static final String GROUP_TABLE_USER_NAME_COLUMN = "group-table-user-name-column";
    public static final String LINK_USER_GROUP_TABLE		= "link-user-group-table";
    public static final String LINK_USER_COLUMN				= "link-user-column";
    public static final String LINK_GROUP_COLUMN			= "link-group-column";
    
    public static final String SALT             = "salt";
    public static final String PEPPER           = "pepper";
}
