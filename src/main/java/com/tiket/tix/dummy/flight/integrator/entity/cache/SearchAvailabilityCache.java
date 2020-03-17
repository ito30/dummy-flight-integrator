package com.tiket.tix.dummy.flight.integrator.entity.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.dummy.flight.integrator.entity.SearchAvailability;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchAvailabilityCache extends AbstractSearchAvailabilityCache {

  private List<SearchAvailability> searchAvailabilities;

  public List<SearchAvailability> getSearchAvailabilities() {
    return searchAvailabilities;
  }
}
