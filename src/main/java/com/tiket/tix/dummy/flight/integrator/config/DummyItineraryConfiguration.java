package com.tiket.tix.dummy.flight.integrator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author ito on 26/02/20
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "dummy.itinerary")
public class DummyItineraryConfiguration {

  private String requestTopic;
  private String responseTopic;
  private String departureUrl;
  private String returnUrl;
  private String smartTripUrl;
  private float emptyListProbability;
}
