package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.common.entity.CommonModel;
import com.tiket.tix.flight.common.model.constant.enums.CabinClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchAvailability extends CommonModel implements Serializable {

  private static final long serialVersionUID = 1L;

  private String flightId;
  private String flightSelect;
  private String currency;
  private String origin;
  private String destination;
  private String date;
  private Airline marketingAirline;
  private int adult;
  private int child;
  private int infant;
  private SearchSchedule departure;
  private SearchSchedule arrival;
  private SearchFareDetail fareDetail;
  private int totalTravelTimeInMinutes;
  private int totalTransitTimeInMinutes;
  private TotalTime totalTravelTime;
  private TotalTime totalTransitTime;
  private Integer totalTransit;
  private boolean isCrossFare;
  private Integer seatAvailability;
  private boolean holdable;
  private boolean upgradable;
  private boolean international;
  private List<ScheduleResponse> schedules;
  private boolean refundable;
  private String journeySellKey;
  private String fareSellKey;
  private String marriageGroup;
  private boolean roundTrip;
  private CabinClass cabinClass;
  private boolean withInsurance;
}
