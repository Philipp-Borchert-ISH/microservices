package de.falconit.microservices.feeds.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;

@ApplicationScoped
public class UtilityProvider
{
    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint)
    {
        return Logger.getLogger(
                injectionPoint.getMember().getDeclaringClass().getName());
    }

}
