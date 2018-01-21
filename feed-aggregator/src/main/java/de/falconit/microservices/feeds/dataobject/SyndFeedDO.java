package de.falconit.microservices.feeds.dataobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.rometools.rome.feed.synd.SyndFeedImpl;

@Entity
public class SyndFeedDO
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne(optional = true)
    private UserDO userDO;

    @Lob
    private SyndFeedImpl syndFeedImpl;

    @Column
    private String url;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public UserDO getUserDO()
    {
        return userDO;
    }

    public void setUserDO(UserDO userDO)
    {
        this.userDO = userDO;
    }

    public SyndFeedImpl getSyndFeedImpl()
    {
        return syndFeedImpl;
    }

    public void setSyndFeedImpl(SyndFeedImpl syndFeedImpl)
    {
        this.syndFeedImpl = syndFeedImpl;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
