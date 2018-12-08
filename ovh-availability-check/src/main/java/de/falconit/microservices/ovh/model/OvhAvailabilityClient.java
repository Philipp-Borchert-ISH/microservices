package de.falconit.microservices.ovh.model;

import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

public class OvhAvailabilityClient
{
    public static final String OVH_SERVER_AVAILABILITY_ENDPOINT = "https://www.ovh.com/engine/api/dedicated/server/availabilities?country=de";

    public static List<OvhServerAvailability> getAvailability(String serverType)
    {
        return ClientBuilder.newClient()
                .target(OVH_SERVER_AVAILABILITY_ENDPOINT)
                .queryParam("hardware", serverType).request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<List<OvhServerAvailability>>()
                {
                });
    }

}
