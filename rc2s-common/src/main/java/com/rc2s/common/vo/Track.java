package com.rc2s.common.vo;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "track")
public class Track implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String path;

    @Column(name="`order`")
    @NotNull
    private short order;

    private Date created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    @NotNull
    private User user;

    public Track() {}

    public Track(String path, short order, Date created)
    {
        this.path = path;
        this.order = order;
        this.created = created;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public short getOrder()
    {
        return order;
    }

    public void setOrder(short order)
    {
        this.order = order;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    @Override
    public String toString()
    {
        return path;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.id);
        hash = 11 * hash + Objects.hashCode(this.path);
        hash = 11 * hash + Objects.hashCode(this.order);
        hash = 11 * hash + Objects.hashCode(this.created);
        return hash;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o != null && o instanceof Track)
        {
            Track t = (Track)o;

            if(t.getId() != null && this.getId() != null)
                return Objects.equals(t.getId(), this.getId());
        }

        return false;

    }
}
