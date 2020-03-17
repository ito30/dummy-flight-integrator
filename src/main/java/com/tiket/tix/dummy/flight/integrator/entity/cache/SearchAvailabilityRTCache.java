package com.tiket.tix.dummy.flight.integrator.entity.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.dummy.flight.integrator.entity.SearchAvailabilityRT;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchAvailabilityRTCache extends AbstractSearchAvailabilityCache {

  private static final long serialVersionUID = 1L;

  private SearchAvailabilityRT searchAvailabilityRT;
}
