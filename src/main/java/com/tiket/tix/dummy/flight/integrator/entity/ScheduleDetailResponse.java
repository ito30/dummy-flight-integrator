package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.common.entity.ObjectHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleDetailResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private String date;
  private String time;
  private Double timezone;
  private String airportCode;
  private String terminal;
  private boolean visaRequired;

  @Override
  public boolean equals(Object obj) {
    return ObjectHelper.equals(this, obj);
  }

  @Override
  public int hashCode() {
    return ObjectHelper.hashCode(this);
  }
}
