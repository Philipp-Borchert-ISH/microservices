package de.falconit.microservices.feeds.service;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import de.falconit.microservices.feeds.dataobject.SyndFeedDO;
import de.falconit.microservices.feeds.persistence.FeedsDAO;

@ApplicationScoped
public class FeedService
{

    public static final int FEED_RETENTION_DAYS = 7;

    @Inject
    private FeedsDAO feedsDAO;

    @Inject
    private Logger log;

    @Transactional
    public void updateAllFeeds()
    {
        List<SyndFeedDO> activeFeeds = feedsDAO.getActiveFeeds();
        SyndFeedInput input = new SyndFeedInput();
        for (SyndFeedDO feedDO : activeFeeds)
        {
            SyndFeed feed = null;
            try
            {
                URLConnection urlCon = new URL(feedDO.getUrl())
                        .openConnection();
                urlCon.setConnectTimeout(10000);
                urlCon.setReadTimeout(10000);
                feed = input.build(new XmlReader(urlCon));
            } catch (Exception e)
            {
                log.errorv(e,
                        "Issue with SyndFeed with id: {0} - provided url: {1}",
                        feedDO.getId(), feedDO.getUrl());
                continue;
            }

            if (feed == null)
            {
                log.error("empty feed received");
                continue;
            }
            Date filterDate = new Date(System.currentTimeMillis()
                    - TimeUnit.DAYS.toMillis(FEED_RETENTION_DAYS));

            Set<SyndEntry> existingEntries = new HashSet<>(feedDO
                    .getSyndFeedImpl().getEntries().stream()
                    .filter(entry -> entry.getPublishedDate() == null
                            || entry.getPublishedDate().after(filterDate))
                    .collect(Collectors.toList()));

            existingEntries.addAll(feed.getEntries());

            feed.setEntries(new ArrayList<>(existingEntries));
            feedDO.setSyndFeedImpl((SyndFeedImpl) feed);
        }

    }

    public void addFeedByURL(URL feedURL)
    {
        SyndFeedInput input = new SyndFeedInput();

        SyndFeed feed;
        try
        {
            URLConnection urlCon = feedURL.openConnection();
            urlCon.setConnectTimeout(10000);
            urlCon.setReadTimeout(10000);

            feed = input.build(new XmlReader(feedURL));
        } catch (Exception e)
        {
            log.errorv(e, "Issue with SyndFeed with url: {0}",
                    feedURL.toExternalForm());
            return;
        }

        if (feed == null)
        {
            log.error("empty feed received");
            return;
        }

        SyndFeedDO feedDO = new SyndFeedDO();
        feedDO.setSyndFeedImpl((SyndFeedImpl) feed);
        feedDO.setUrl(feedURL.toExternalForm());

        feedsDAO.persistFeedDO(feedDO);
    }

    public SyndFeed getAggregatedFeed()
    {
        List<SyndFeedDO> activeFeeds = feedsDAO.getActiveFeeds();

        SyndFeed aggFeed = new SyndFeedImpl();
        if (activeFeeds.size() < 1)
        {
            return aggFeed;
        }
        Iterator<SyndFeedDO> iterator = activeFeeds.iterator();
        aggFeed.copyFrom(iterator.next().getSyndFeedImpl());

        while (iterator.hasNext())
        {
            aggFeed.getEntries()
                    .addAll(iterator.next().getSyndFeedImpl().getEntries());
        }

        return aggFeed;

    }

    public SyndFeedDO getFeedByURL(String url)
    {
        return feedsDAO.findByURL(url);
    }

    public SyndFeed getFeedById(long id)
    {
        return feedsDAO.findById(id);
    }

    public List<SyndFeedDO> getActiveFeeds()
    {
        return feedsDAO.getActiveFeeds();
    }
}
