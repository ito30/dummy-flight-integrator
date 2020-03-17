package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tiket.tix.common.entity.CommonModel;
import com.tiket.tix.flight.common.model.constant.enums.CabinClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ScheduleResponseDeserializer.class)
public class ScheduleResponse extends CommonModel implements Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  private String fareClass;
  private String airline;
  private String flightNumber;
  private String aircraft;
  private CabinClass cabinClass;
  private ScheduleDetailResponse departureDetail;
  private ScheduleDetailResponse arrivalDetail;
  private int totalTravelTimeInMinutes;
  private int totalTransitTimeInMinutes;
  private TotalTime travelTime;
  private TotalTime transitTime;
  private BaggageResponse baggage;
  private List<Connecting> connectings;
  private boolean facilitiesReady;
  private Map<String, String> facilitiesValue;
  private Map<String, Object> facilities;
  private List<String> facilitiesPriority;
  private String operatingAirline;
  private String operatingFlightNumber;
  private boolean bundlingMeal = false;
  private boolean bundlingBaggage = false;

  public ScheduleResponse() {
    facilities = new HashMap<>();
  }
}
