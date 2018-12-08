package de.falconit.microservices.ovh.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OvhServerAvailability
{
    @JsonProperty("hardware")
    private String hardware;

    @JsonProperty("region")
    private String region;

    @JsonProperty("datacenters")
    private List<Datacenter> datacenters = new ArrayList<>();

    public String getHardware()
    {
        return hardware;
    }

    public void setHardware(String hardware)
    {
        this.hardware = hardware;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public List<Datacenter> getDatacenters()
    {
        return datacenters;
    }

    public void setDatacenters(List<Datacenter> datacenters)
    {
        this.datacenters = datacenters;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Datacenter
    {
        @JsonProperty("availability")
        private String availability;
        @JsonProperty("datacenter")
        private String datancenter;

        public String getAvailability()
        {
            return availability;
        }

        public void setAvailability(String availability)
        {
            this.availability = availability;
        }

        public String getDatancenter()
        {
            return datancenter;
        }

        public void setDatancenter(String datancenter)
        {
            this.datancenter = datancenter;
        }
    }
}
