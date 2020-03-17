package com.tiket.tix.dummy.flight.integrator.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tiket.tix.common.libraries.BeanMapper;
import com.tiket.tix.common.rest.web.model.request.MandatoryRequest;
import com.tiket.tix.dummy.flight.integrator.config.DummyItineraryConfiguration;
import com.tiket.tix.dummy.flight.integrator.entity.SearchAvailability;
import com.tiket.tix.dummy.flight.integrator.entity.kafka.SearchAvailabilityKafkaResponse;
import com.tiket.tix.dummy.flight.integrator.outbound.KafkaProducerService;
import com.tiket.tix.flight.common.model.constant.enums.TripType;
import com.tiket.tix.flight.common.model.request.fare.IntegratorFareRequest;
import com.tiket.tix.flight.common.model.request.fare.Route;
import com.tiket.tix.flight.common.model.request.kafka.KafkaIntegratorFareRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.tiket.tix.dummy.flight.integrator.entity.constant.CacheConstant.KEY_SEPARATOR;

/**
 * @author ito on 26/02/20
 */
@Component
@Slf4j
public class DummyIntegratorInboundService {

  @Autowired
  private KafkaProducerService kafkaProducerService;

  @Autowired
  private DummyItineraryConfiguration dummyItineraryConfiguration;

  static class JSONHelper {

    ObjectMapper mapper;

    JSONHelper() {
      mapper = new ObjectMapper();
      mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    <T> String convertObjectToJsonInString(T data) throws JsonProcessingException {
      return mapper.writeValueAsString(data);
    }

    <T> T convertJsonInStringToObject(String jsonInString, Class<T> clazz) throws IOException {
      return mapper.readValue(jsonInString, clazz);
    }

    ObjectMapper getMapper() {
      return mapper;
    }
  }

  class Counter {
    int n;
  }

  private JSONHelper jsonHelper = new JSONHelper();
  private SearchAvailabilityKafkaResponse departureCache;
  private SearchAvailabilityKafkaResponse smartTripCache;

  private final OkHttpClient httpClient = new OkHttpClient();
  private static final String UNEXPECTED_CODE_ERROR = "Unexpected code ";

  @KafkaListener(
      topics = "${dummy.itinerary.request-topic}",
      containerFactory = "kafkaListenerContainerFactory")
  public void listen(ConsumerRecord<String, String> record) {
    Mono.fromCallable(() -> jsonHelper.convertJsonInStringToObject(record.value(), KafkaIntegratorFareRequest.class))
        .flatMap(kafkaIntegratorFareRequest -> {
          log.info("#publishToKafka topic: {}", dummyItineraryConfiguration.getResponseTopic());
          MandatoryRequest mandatoryRequest = kafkaIntegratorFareRequest.getMandatoryRequest();

          log.info("airlines request: {}", kafkaIntegratorFareRequest.getIntegratorFareRequests().stream()
              .map(integratorFareRequest -> integratorFareRequest.getSupplierRequest().getAirlines())
              .collect(Collectors.toList()));

          List<Mono<Void>> publishMonos = new ArrayList<>();
          Counter counter = new Counter();
          counter.n = 0;

          for (IntegratorFareRequest integratorFareRequest : kafkaIntegratorFareRequest.getIntegratorFareRequests()) {
            for (String marketingAirline : integratorFareRequest.getSupplierRequest().getAirlines()) {
              if (integratorFareRequest.getTripTypes().contains(TripType.ROUND_TRIP) &&
                  integratorFareRequest.getSupplierRequest().getAccounts().get(0).getCode().toLowerCase().contains("sabre")) {
                Pair<String, SearchAvailabilityKafkaResponse> keyAndCachePair = buildCacheKeyRTAndCache(mandatoryRequest.getStoreId(), integratorFareRequest, marketingAirline);
                prepareAndPublish(publishMonos, keyAndCachePair, marketingAirline, counter, TripType.ROUND_TRIP);

                continue;
              }

              List<Pair<String, SearchAvailabilityKafkaResponse>> keyAndCachePairs = new ArrayList<>();
              for (Route route : integratorFareRequest.getRoutes()) {
                if (integratorFareRequest.getTripTypes().contains(TripType.ROUND_TRIP)) {
                  keyAndCachePairs.add(buildCacheKeyRtsAndCache(mandatoryRequest.getStoreId(), route, integratorFareRequest, marketingAirline));
                } else {
                  keyAndCachePairs.add(buildCacheKeyOwAndCache(mandatoryRequest.getStoreId(), route, integratorFareRequest, marketingAirline));
                }
              }

              prepareAndPublish(publishMonos, keyAndCachePairs, marketingAirline, counter);
            }
          }

          log.info("Loaded itineraries count: {}", counter.n);
          return zipMonoList(publishMonos);
        }).subscribe();
  }

  private void prepareAndPublish(
      List<Mono<Void>> publishMonos, Pair<String, SearchAvailabilityKafkaResponse> pairKeyAndCache,
      String marketingAirline, Counter counter, TripType tripType) {
    String cacheKey = pairKeyAndCache.getKey();
    SearchAvailabilityKafkaResponse kafkaResponse = pairKeyAndCache.getValue();
    kafkaResponse.setCacheKey(cacheKey);

    if (tripType.equals(TripType.ROUND_TRIP)) {
      kafkaResponse.getSearchAvailabilityRTCache().setMarketingAirline(marketingAirline);
    } else {
      kafkaResponse.getSearchAvailabilityCache().setMarketingAirline(marketingAirline);
    }

    try {
      boolean shouldContainedCache = getRandomBoolean();

      if (shouldContainedCache) counter.n++;

      publishMonos.add(kafkaProducerService.send(
          dummyItineraryConfiguration.getResponseTopic(), jsonHelper.convertObjectToJsonInString(
              determineCacheContent(shouldContainedCache, kafkaResponse))));
    } catch (IOException e) {
      log.error("Failed to convertObjectToJsonInString or publish", e);
    }
  }

  private void prepareAndPublish(
      List<Mono<Void>> publishMonos, List<Pair<String, SearchAvailabilityKafkaResponse>> keyAndCachePairs,
      String marketingAirline, Counter counter) {
    for (Pair<String, SearchAvailabilityKafkaResponse> pair : keyAndCachePairs) {
      prepareAndPublish(publishMonos, pair, marketingAirline, counter, TripType.DEPARTURE);
    }
  }

  private SearchAvailabilityKafkaResponse determineCacheContent(boolean shouldContainedCache, SearchAvailabilityKafkaResponse searchAvailabilityKafkaResponse) {
    if (!shouldContainedCache) {
      try {
        SearchAvailabilityKafkaResponse emptyKafkaResponse = jsonHelper.getMapper()
            .readValue(jsonHelper.convertObjectToJsonInString(searchAvailabilityKafkaResponse), SearchAvailabilityKafkaResponse.class);
        if (Objects.nonNull(emptyKafkaResponse.getSearchAvailabilityCache())) {
          emptyKafkaResponse.getSearchAvailabilityCache().setSearchAvailabilities(new ArrayList<>());
        }

        if (Objects.nonNull(emptyKafkaResponse.getSearchAvailabilityRTCache())) {
          emptyKafkaResponse.getSearchAvailabilityRTCache().getSearchAvailabilityRT().setDepartureFlights(new ArrayList<>());
          emptyKafkaResponse.getSearchAvailabilityRTCache().getSearchAvailabilityRT().setReturnFlights(new ArrayList<>());
          emptyKafkaResponse.getSearchAvailabilityRTCache().getSearchAvailabilityRT().setFareDetail(new HashMap<>());
        }

        return emptyKafkaResponse;
      } catch (JsonProcessingException e) {
        log.error("Failed to clone searchAvailabilityKafkaResponse", e);
      }
    }

    return searchAvailabilityKafkaResponse;
  }

  @PostConstruct
  public void predefinedDummyResponse() throws IOException {
    Request request = new Request.Builder()
        .url(dummyItineraryConfiguration.getDepartureUrl())
        .get()
        .build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful() || Objects.isNull(response.body()))
        throw new IOException(UNEXPECTED_CODE_ERROR + response);
      departureCache = jsonHelper.convertJsonInStringToObject(response.body().string(), SearchAvailabilityKafkaResponse.class);
    }

    request = new Request.Builder()
        .url(dummyItineraryConfiguration.getSmartTripUrl())
        .get()
        .build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful() || Objects.isNull(response.body()))
        throw new IOException(UNEXPECTED_CODE_ERROR + response);
      smartTripCache = jsonHelper.convertJsonInStringToObject(response.body().string(), SearchAvailabilityKafkaResponse.class);
    }
  }

  private String buildCacheKeyOw(String storeId, Route route, IntegratorFareRequest integratorFareRequest, String airline) {
    return route.getOrigin().concat(KEY_SEPARATOR)
        .concat(route.getDestination()).concat(KEY_SEPARATOR)
        .concat(route.getDepartureDate()).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getAdult())).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getChild())).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getInfant())).concat(KEY_SEPARATOR)
        .concat(airline).concat(KEY_SEPARATOR)
        .concat(integratorFareRequest.getCabinClass().name()).concat(KEY_SEPARATOR)
        .concat(storeId);
  }

  private String buildCacheKeyRts(String storeId, Route route, IntegratorFareRequest integratorFareRequest, String airline) {
    return route.getOrigin().concat(KEY_SEPARATOR)
        .concat(route.getDestination()).concat(KEY_SEPARATOR)
        .concat(route.getDepartureDate()).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getAdult())).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getChild())).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getInfant())).concat(KEY_SEPARATOR)
        .concat(airline).concat(KEY_SEPARATOR)
        .concat(integratorFareRequest.getCabinClass().name()).concat(KEY_SEPARATOR)
        .concat(storeId).concat(KEY_SEPARATOR)
        .concat("RTS");
  }

  private Pair<String, SearchAvailabilityKafkaResponse> buildCacheKeyOwAndCache(
      String storeId, Route route, IntegratorFareRequest integratorFareRequest, String marketingAirline) {
    String cacheKey = buildCacheKeyOw(storeId, route, integratorFareRequest, marketingAirline);

    return Pair.of(cacheKey, buildKafkaResponseDataOw(route, integratorFareRequest, marketingAirline));
  }

  private Pair<String, SearchAvailabilityKafkaResponse> buildCacheKeyRtsAndCache(
      String storeId, Route route, IntegratorFareRequest integratorFareRequest, String marketingAirline) {
    String cacheKey = buildCacheKeyRts(storeId, route, integratorFareRequest, marketingAirline);

    return Pair.of(cacheKey, buildKafkaResponseDataOw(route, integratorFareRequest, marketingAirline));
  }

  private Pair<String, SearchAvailabilityKafkaResponse> buildCacheKeyRTAndCache(String storeId, IntegratorFareRequest integratorFareRequest, String marketingAirline) {
    String cacheKey = integratorFareRequest.getOrigin().concat(KEY_SEPARATOR)
        .concat(integratorFareRequest.getDestination()).concat(KEY_SEPARATOR)
        .concat(integratorFareRequest.getDepartureDate()).concat(KEY_SEPARATOR)
        .concat(integratorFareRequest.getReturnDate()).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getAdult())).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getChild())).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getInfant())).concat(KEY_SEPARATOR)
        .concat(marketingAirline).concat(KEY_SEPARATOR)
        .concat(integratorFareRequest.getCabinClass().name()).concat(KEY_SEPARATOR)
        .concat(storeId);

    return Pair.of(cacheKey, buildKafkaResponseDataRT(integratorFareRequest, marketingAirline));
  }

  private SearchAvailabilityKafkaResponse buildKafkaResponseDataRT(IntegratorFareRequest integratorFareRequest, String marketingAirline) {
    SearchAvailabilityKafkaResponse smartTripKafkaResponse = BeanMapper.map(smartTripCache, SearchAvailabilityKafkaResponse.class);

    List<SearchAvailability> depSearchAvailabilities = smartTripKafkaResponse.getSearchAvailabilityRTCache().getSearchAvailabilityRT().getDepartureFlights();
    List<SearchAvailability> returnSearchAvailabilities = smartTripKafkaResponse.getSearchAvailabilityRTCache().getSearchAvailabilityRT().getReturnFlights();
    for (int i = 0; i < depSearchAvailabilities.size(); i++) {
      modifyAvailabilityDataRT(depSearchAvailabilities.get(i), returnSearchAvailabilities.get(i), integratorFareRequest, marketingAirline);
    }

    return smartTripKafkaResponse;
  }

  private SearchAvailabilityKafkaResponse buildKafkaResponseDataOw(
      Route route, IntegratorFareRequest integratorFareRequest, String marketingAirline) {
    SearchAvailabilityKafkaResponse departureKafkaResponse = BeanMapper.map(departureCache, SearchAvailabilityKafkaResponse.class);

    departureKafkaResponse.getSearchAvailabilityCache().getSearchAvailabilities()
        .forEach(searchAvailability -> modifyAvailabilityDataOw(searchAvailability, route, integratorFareRequest, marketingAirline));

    return departureKafkaResponse;
  }

  private void modifyAvailabilityDataRT(
      SearchAvailability depSearchAvailability, SearchAvailability returnSearchAvailability, IntegratorFareRequest integratorFareRequest, String marketingAirline) {
    Route depRoute = integratorFareRequest.getRoutes().get(0);
    Route returnRoute = integratorFareRequest.getRoutes().get(1);

    modifyAvailabilityData(depSearchAvailability, depRoute, integratorFareRequest, marketingAirline);
    modifyAvailabilityData(returnSearchAvailability, returnRoute, integratorFareRequest, marketingAirline);

    depSearchAvailability.setFlightId(buildFlightIdRT(depSearchAvailability, returnSearchAvailability, integratorFareRequest));
    returnSearchAvailability.setFlightId(buildFlightIdRT(depSearchAvailability, returnSearchAvailability, integratorFareRequest));
  }

  private void modifyAvailabilityDataOw(SearchAvailability searchAvailability, Route route, IntegratorFareRequest integratorFareRequest, String marketingAirline) {
    modifyAvailabilityData(searchAvailability, route, integratorFareRequest, marketingAirline);
    searchAvailability.setFlightId(buildFlightIdOw(searchAvailability, route, integratorFareRequest));
  }

  private void modifyAvailabilityData(
      SearchAvailability searchAvailability, Route route, IntegratorFareRequest integratorFareRequest, String marketingAirline) {
    searchAvailability.setOrigin(route.getOrigin());
    searchAvailability.setDestination(route.getDestination());
    searchAvailability.setDate(route.getDepartureDate());
    searchAvailability.getDeparture().setDate(route.getDepartureDate());
    searchAvailability.getDeparture().setAirportCode(route.getOrigin());
    searchAvailability.getArrival().setDate(route.getDepartureDate());
    searchAvailability.getArrival().setAirportCode(route.getDestination());
    searchAvailability.setAdult(integratorFareRequest.getAdult());
    searchAvailability.setChild(integratorFareRequest.getChild());
    searchAvailability.setInfant(integratorFareRequest.getInfant());

    int schedulesSize = searchAvailability.getSchedules().size();
    searchAvailability.getSchedules().get(0).getDepartureDetail().setAirportCode(route.getOrigin());
    searchAvailability.getSchedules().get(0).setAirline(marketingAirline);

    searchAvailability.getSchedules().get(schedulesSize - 1).getArrivalDetail().setAirportCode(route.getDestination());
    searchAvailability.getSchedules().get(schedulesSize - 1).setAirline(marketingAirline);
  }

  private String buildFlightIdOw(SearchAvailability searchAvailability, Route route, IntegratorFareRequest integratorFareRequest) {
    // searchAvailability's airline has been modified

    return route.getOrigin().concat(KEY_SEPARATOR)
        .concat(route.getDestination()).concat(KEY_SEPARATOR)
        .concat(route.getDepartureDate()).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getAdult())).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getChild())).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getInfant())).concat(KEY_SEPARATOR)
        .concat(buildFlightNumbers(searchAvailability)).concat(KEY_SEPARATOR)
        .concat(integratorFareRequest.getCabinClass().name());
  }

  private String buildFlightIdRT(
      SearchAvailability departureSearchAvailability, SearchAvailability returnSearchAvailability, IntegratorFareRequest integratorFareRequest) {
    // searchAvailability's airline has been modified

    List<Route> routes = integratorFareRequest.getRoutes();

    return routes.get(0).getOrigin().concat(KEY_SEPARATOR)
        .concat(routes.get(0).getDestination()).concat(KEY_SEPARATOR)
        .concat(routes.get(0).getDepartureDate()).concat(KEY_SEPARATOR)
        .concat(routes.get(1).getDepartureDate()).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getAdult())).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getChild())).concat(KEY_SEPARATOR)
        .concat(Integer.toString(integratorFareRequest.getInfant())).concat(KEY_SEPARATOR)
        .concat(buildFlightNumbers(departureSearchAvailability)).concat(KEY_SEPARATOR)
        .concat(buildFlightNumbers(returnSearchAvailability)).concat(KEY_SEPARATOR)
        .concat(integratorFareRequest.getCabinClass().name());
  }

  private String buildFlightNumbers(SearchAvailability searchAvailability) {
    return searchAvailability.getSchedules().stream()
        .map(schedule -> schedule.getAirline().concat(schedule.getFlightNumber()))
        .collect(Collectors.joining("-"));
  }

  @SuppressWarnings("unchecked")
  private <T> Mono<List<T>> zipMonoList(List<Mono<T>> singleList) {
    if (CollectionUtils.isEmpty(singleList)) {
      return Mono.just(new ArrayList<>());
    }

    return Mono.zip(
        singleList, objects -> {
          List<T> list = new ArrayList<>();
          for (Object object : objects) {
            list.add((T) object);
          }

          return list;
        });
  }

  private boolean getRandomBoolean() {
    double randomValue = Math.random() * 100;  //0.0 to 99.9
    return randomValue <= dummyItineraryConfiguration.getEmptyListProbability();
  }

}
