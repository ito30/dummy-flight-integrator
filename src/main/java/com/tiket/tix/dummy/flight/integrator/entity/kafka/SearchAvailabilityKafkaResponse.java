package com.tiket.tix.dummy.flight.integrator.entity.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.common.entity.CommonModel;
import com.tiket.tix.common.rest.web.model.request.MandatoryRequest;
import com.tiket.tix.dummy.flight.integrator.entity.cache.SearchAvailabilityCache;
import com.tiket.tix.dummy.flight.integrator.entity.cache.SearchAvailabilityRTCache;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by ito on Jan 28, 2019
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchAvailabilityKafkaResponse extends CommonModel {
  private MandatoryRequest mandatoryRequest;
  private String cacheKey;
  private SearchAvailabilityCache searchAvailabilityCache;
  private SearchAvailabilityRTCache searchAvailabilityRTCache;
  private int ttl;
}
