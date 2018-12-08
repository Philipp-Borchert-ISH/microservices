package de.falconit.microservices.ovh.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.falconit.microservices.ovh.model.OvhServerAvailability.Datacenter;

@Singleton
public class OvhAvailabilitySchedulerBean
{
    private static final String REGION = "europe";

    @Inject
    @ConfigurationValue("product")
    private String SERVER_TYPE;
    
    @EJB
    private MailServiceBean mailServiceBean;

    private static final Logger log = Logger
            .getLogger(OvhAvailabilitySchedulerBean.class);

    private boolean wasAvailable = false;

    @Schedule(persistent = false, hour = "*", minute = "*/2", second = "0")
    public void checkAvailability()
    {
        List<OvhServerAvailability> availability = OvhAvailabilityClient
                .getAvailability(SERVER_TYPE);

        List<Datacenter> availableDatacenters = availability.stream()
                .filter(av -> av.getRegion().equals(REGION))
                .flatMap(av -> av.getDatacenters().stream())
                .filter(dc -> !dc.getAvailability().equals("unavailable"))
                .collect(Collectors.toList());

        if (availableDatacenters.size() > 0 && !wasAvailable)
        {
            ObjectMapper om = new ObjectMapper();
            try
            {
                mailServiceBean.sendMessage(
                        "OVH Availabilty Check - Server IS now available: "
                                + SERVER_TYPE,
                        om.writeValueAsString(availableDatacenters));
            } catch (Exception e)
            {
                log.error("unknown error", e);
            }
            wasAvailable = true;
        } else if (availableDatacenters.size() == 0 && wasAvailable)
        {
            try
            {
                mailServiceBean.sendMessage(
                        "OVH Availabilty Check - Server is NOT available: "
                                + SERVER_TYPE,
                        "---");
            } catch (Exception e)
            {
                log.error("unknown error", e);
            }
            wasAvailable = false;
        }

    }

}
