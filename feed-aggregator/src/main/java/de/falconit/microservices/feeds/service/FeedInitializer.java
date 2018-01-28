package de.falconit.microservices.feeds.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.api.config.Resolver;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

@Startup
@Singleton
public class FeedInitializer

{
    @Inject
    @ConfigurationValue("feeds")
    private Resolver<String> feedResolver;

    @Inject
    private Logger log;

    @Inject
    private FeedService feedService;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostConstruct
    public void loadFeeds()
    {
        if (!feedResolver.hasValue())
        {
            log.warn("no feeds found");
        }

        Resolver<List> as = feedResolver.as(List.class);
        List<String> feeds = as.getValue();

        for (String feedURL : feeds)
        {
            if (feedService.getFeedByURL(feedURL) != null)
            {
                log.infov("Feed exists: {0}", feedURL);
                continue;
            }

            URL feed;
            try
            {
                feed = new URL(feedURL);
            } catch (MalformedURLException e)
            {
                log.error("issue with feed url", e);
                continue;
            }

            log.infov("Adding new feed by URL: {0}", feedURL);
            feedService.addFeedByURL(feed);
        }
    }

    @Schedule(minute = "*/5", second="0", hour="*", dayOfMonth="*")
    public void refreshFeeds()
    {
        log.info("refreshing feeds");
        feedService.updateAllFeeds();
    }
}
