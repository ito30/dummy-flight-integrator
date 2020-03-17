package com.tiket.tix.dummy.flight.integrator.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "kafka.producer.dummy-itinerary")
@Getter
@Setter
public class KafkaProducerConfiguration {

  private String bootstrapServers;
  private int producerMaxBlockMs;
  private int maxRequestSizeConfig;

  @Bean
  public Map<String, Object> producerConfigs() {
    return producerConfigs(bootstrapServers, producerMaxBlockMs, maxRequestSizeConfig);
  }

  @Bean
  public Map<String, Object> producerConfigsStr() {
    return producerConfigsStr(bootstrapServers, producerMaxBlockMs, maxRequestSizeConfig);

  }

  @Bean
  public ProducerFactory<Integer, String> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  @Bean
  public ProducerFactory<String, String> producerFactoryStr() {
    return new DefaultKafkaProducerFactory<>(producerConfigsStr());
  }

  @Bean
  public KafkaTemplate<Integer, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplateStr() {
    return new KafkaTemplate<>(producerFactoryStr());
  }

  private Map<String, Object> producerConfigs(
      String bootstrapServers, int producerMaxBlockMs, int maxRequestSizeConfig) {
    Map<String, Object> props = new HashMap<>();

    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    // value to block, after which it will throw a TimeoutException
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, producerMaxBlockMs);
    props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSizeConfig);

    return props;
  }

  private Map<String, Object> producerConfigsStr(
      String bootstrapServers, int producerMaxBlockMs, int maxRequestSizeConfig) {
    Map<String, Object> props = new HashMap<>();

    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    // value to block, after which it will throw a TimeoutException
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, producerMaxBlockMs);
    props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSizeConfig);

    return props;
  }
}