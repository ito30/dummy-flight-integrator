package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.common.entity.CommonModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Connecting extends CommonModel implements Serializable {

  private static final long serialVersionUID = 1L;

  private String arrivalTime;
  private String departureTime;
  private Double timezone;
  private String airportName;
  private String airportCode;
  private String cityName;
  private String cityCode;
  private String departureTerminal;
  private TotalTime totalTime;
  private int connectingTime;
}
