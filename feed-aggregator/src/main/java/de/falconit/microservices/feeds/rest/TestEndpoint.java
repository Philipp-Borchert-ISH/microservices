package de.falconit.microservices.feeds.rest;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.w3c.dom.Document;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedOutput;

import de.falconit.microservices.feeds.service.FeedService;

@ApplicationScoped
@Path("/hello")
public class TestEndpoint
{

    @Inject
    private FeedService feedService;

    @GET
    @Produces("text/xml")
    public Response doGet() throws Exception
    {
        feedService.addFeedByURL(new URL("http://www.feedforall.com/sample.xml"));
        feedService.addFeedByURL(
                new URL("http://www.feedforall.com/blog-feed.xml"));
        feedService.addFeedByURL(
                new URL("http://www.feedforall.com/sample-feed.xml"));
        feedService.updateAllFeeds();
        SyndFeedOutput output = new SyndFeedOutput();
        SyndFeed aggregatedFeed = feedService.getAggregatedFeed();
        Collections.sort(aggregatedFeed.getEntries(),
                new Comparator<SyndEntry>()
                {
                    @Override
                    // Desc order
                    public int compare(SyndEntry o2, SyndEntry o1)
                    {
                        if (o1.getPublishedDate() == null)
                        {
                            return o2.getPublishedDate() == null ? 0 : -1;
                        }

                        return o2.getPublishedDate() != null
                                ? o1.getPublishedDate().compareTo(
                                        o2.getPublishedDate())
                                : 1;
                    }
                });

        Document outputW3CDom = output
                .outputW3CDom(aggregatedFeed);

        return Response.ok().entity(outputW3CDom).build();
    }
}