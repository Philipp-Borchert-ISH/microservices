package de.falconit.microservices.feeds.rest;

import java.util.Collections;
import java.util.Comparator;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.w3c.dom.Document;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedOutput;

import de.falconit.microservices.feeds.service.FeedInitializer;
import de.falconit.microservices.feeds.service.FeedService;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
@Path("/feeds")
public class FeedServiceWeb
{

    @Inject
    private FeedService feedService;

    @EJB
    private FeedInitializer initializer;

    @GET
    @Produces("text/xml")
    public Response getAllFeeds() throws Exception
    {
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

        Document outputW3CDom = output.outputW3CDom(aggregatedFeed);

        return Response.ok().entity(outputW3CDom).build();
    }

    @GET
    @Path("/{feedId}")
    @Produces("text/xml")
    public Response getFeedById(@PathParam("feedId") long feedId)
            throws Exception
    {
        SyndFeedOutput output = new SyndFeedOutput();

        return Response.ok()
                .entity(output.outputW3CDom(feedService.getFeedById(feedId)))
                .build();
    }

    @GET
    @Path("/list")
    @Produces("text/html")
    public Response getFeedList()
    {
        StringBuilder sb = new StringBuilder("<html><body><table>");
        feedService.getActiveFeeds().forEach(feed -> {
            sb.append("<tr><td>");
            sb.append(feed.getId());
            sb.append("</td><td>");
            sb.append(feed.getUrl());
            sb.append("</td></tr>");
        });

        sb.append("</table></body></html>");

        return Response.ok().entity(sb.toString()).build();
    }

}