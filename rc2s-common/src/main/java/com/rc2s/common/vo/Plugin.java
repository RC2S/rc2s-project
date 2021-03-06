package com.rc2s.common.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "plugin")
public class Plugin implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String name;
	private String version;
	private String author;
	private String status;
	private Boolean activated;
	private String access;
	
	private Date created;
	private Date updated;
	
	public Plugin() {}

	public Plugin(final Integer id, final String name, final String version, 
            final String author, final String status, final Boolean activated, 
            final String access, final Date created, final Date updated)
	{
		this.id = id;
		this.name = name;
		this.version = version;
		this.author = author;
		this.status = status;
		this.activated = activated;
		this.access = access;
		this.created = created;
		this.updated = updated;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(final Integer id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(final String version)
	{
		this.version = version;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(final String author)
	{
		this.author = author;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(final String status)
	{
		this.status = status;
	}

	public Boolean isActivated()
	{
		return activated;
	}

	public void setActivated(final Boolean activated)
	{
		this.activated = activated;
	}

	public String getAccess()
	{
		return access;
	}

	public void setAccess(final String access)
	{
		this.access = access;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(final Date created)
	{
		this.created = created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}
	
	@Override
	public String toString()
	{
		return name + " (" + author + ")";
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 29 * hash + Objects.hashCode(this.id);
		hash = 29 * hash + Objects.hashCode(this.name);
		hash = 29 * hash + Objects.hashCode(this.version);
		hash = 29 * hash + Objects.hashCode(this.author);
		hash = 29 * hash + Objects.hashCode(this.status);
		hash = 29 * hash + Objects.hashCode(this.activated);
		hash = 29 * hash + Objects.hashCode(this.access);
		hash = 29 * hash + Objects.hashCode(this.created);
		hash = 29 * hash + Objects.hashCode(this.updated);
		return hash;
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(o != null && o instanceof Plugin)
		{
			Plugin p = (Plugin)o;
			
			if(p.getId() != null && this.getId() != null)
				return Objects.equals(p.getId(), this.getId());
		}
		
		return false;
	}
}