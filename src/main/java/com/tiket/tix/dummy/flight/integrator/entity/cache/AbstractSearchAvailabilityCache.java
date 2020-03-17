package com.tiket.tix.dummy.flight.integrator.entity.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.common.entity.ObjectHelper;
import com.tiket.tix.flight.common.model.request.fare.IntegratorFareRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ito on Aug 05, 2019
 */
@Getter
@Setter
@SuperBuilder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractSearchAvailabilityCache implements Serializable {

  private static final long serialVersionUID = 1L;

  private String marketingAirline;
  private IntegratorFareRequest integratorFareRequest;
  private String distributionType;
  private boolean completed;
  private Date created;

  @Override
  public boolean equals(Object obj) {
    return ObjectHelper.equals(this, obj);
  }

  @Override
  public int hashCode() {
    return ObjectHelper.hashCode(this);
  }
}
