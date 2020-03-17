package com.tiket.tix.dummy.flight.integrator.outbound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service("kafkaProducerSearchService")
@Slf4j
public class KafkaProducerService {

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplateStr;

  @Autowired
  @Qualifier("kafka-publisher")
  private Scheduler kafkaPublisher;

  public Mono<Void> send(String topic, String payload) {
    return Mono.<Void>create(emitter -> {

//      log.info("#publishToKafka topic: {}", topic);
      kafkaTemplateStr.send(topic, payload).addCallback(success -> emitter.success(), emitter::error);
    }).subscribeOn(kafkaPublisher);
  }
}
