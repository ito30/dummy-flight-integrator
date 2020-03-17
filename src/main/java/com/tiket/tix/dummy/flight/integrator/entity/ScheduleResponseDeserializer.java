package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tiket.tix.flight.common.model.constant.enums.CabinClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ito on Aug 05, 2019
 */
public class ScheduleResponseDeserializer extends StdDeserializer<ScheduleResponse> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleResponseDeserializer.class);

  private ObjectMapper mapper = new ObjectMapper();

  public ScheduleResponseDeserializer() {
    this(null);
  }

  public ScheduleResponseDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public ScheduleResponse deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);

    String fareClass = node.get("fareClass").asText();
    String airline = getAirlineCode(node, "airline");
    String flightNumber = node.get("flightNumber").asText();
    String aircraft = node.get("aircraft").asText();
    CabinClass cabinClass = mapper.convertValue(node.get("cabinClass"), CabinClass.class);
    ScheduleDetailResponse departureDetail = mapper.convertValue(node.get("departureDetail"), ScheduleDetailResponse.class);
    ScheduleDetailResponse arrivalDetail = mapper.convertValue(node.get("arrivalDetail"), ScheduleDetailResponse.class);
    int totalTravelTimeInMinutes = node.get("totalTravelTimeInMinutes").intValue();
    int totalTransitTimeInMinutes = node.get("totalTransitTimeInMinutes").intValue();
    TotalTime travelTime = mapper.convertValue(node.get("travelTime"), TotalTime.class);
    TotalTime transitTime = mapper.convertValue(node.get("transitTime"), TotalTime.class);
    BaggageResponse baggage = mapper.convertValue(node.get("baggage"), BaggageResponse.class);
    List<Connecting> connectings = Stream.of(mapper.convertValue(node.get("connectings"), Connecting[].class))
        .collect(Collectors.toList());
    boolean facilitiesReady = node.get("facilitiesReady").booleanValue();

    Map<String, String> facilitiesValue = mapper.convertValue(node.get("facilitiesValue"), Map.class);
    Map<String, Object> facilities = mapper.convertValue(node.get("facilities"), Map.class);
    List<String> facilitiesPriority = Stream.of(mapper.convertValue(node.get("facilitiesPriority"), String[].class))
        .collect(Collectors.toList());
    String operatingAirline = getAirlineCode(node, "operatingAirline");
    String operatingFlightNumber = node.get("operatingFlightNumber").asText();

    boolean bundlingMeal = node.get("bundlingMeal").booleanValue();
    boolean bundlingBaggage = node.get("bundlingBaggage").booleanValue();

    return ScheduleResponse.builder()
        .fareClass(fareClass)
        .airline(airline)
        .flightNumber(flightNumber)
        .aircraft(aircraft)
        .cabinClass(cabinClass)
        .departureDetail(departureDetail)
        .arrivalDetail(arrivalDetail)
        .totalTravelTimeInMinutes(totalTravelTimeInMinutes)
        .totalTransitTimeInMinutes(totalTransitTimeInMinutes)
        .travelTime(travelTime)
        .transitTime(transitTime)
        .baggage(baggage)
        .connectings(connectings)
        .facilitiesReady(facilitiesReady)
        .facilitiesValue(facilitiesValue)
        .facilities(facilities)
        .facilitiesPriority(facilitiesPriority)
        .operatingAirline(operatingAirline)
        .operatingFlightNumber(operatingFlightNumber)
        .bundlingMeal(bundlingMeal)
        .bundlingBaggage(bundlingBaggage)
        .build();
  }

  private String getAirlineCode(JsonNode node, String fieldName) {
    String airlineInString;
    JsonNode airlineNode = node.get(fieldName);

    try {
      if (airlineNode.isObject()) {
        Airline airline = mapper.convertValue(airlineNode, Airline.class);
        airlineInString = airline.getCode();
      } else {
        airlineInString = airlineNode.asText();
      }
    } catch (Exception e) {
      LOGGER.warn("Failed to convertValue to Airline", e);
      airlineInString = airlineNode.asText();
    }

    return airlineInString;
  }
}
