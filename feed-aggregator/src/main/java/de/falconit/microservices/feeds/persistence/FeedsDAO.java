package de.falconit.microservices.feeds.persistence;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import de.falconit.microservices.feeds.dataobject.SyndFeedDO;

@ApplicationScoped
@Transactional
public class FeedsDAO
{
    @PersistenceContext(unitName = "FeedPU")
    private EntityManager em;

    public void persistFeedDO(SyndFeedDO syndFeedDO)
    {
        em.persist(syndFeedDO);
    }

    public List<SyndFeedDO> getActiveFeeds()
    {
        return em.createQuery("FROM SyndFeedDO", SyndFeedDO.class)
                .getResultList();
    }

}
