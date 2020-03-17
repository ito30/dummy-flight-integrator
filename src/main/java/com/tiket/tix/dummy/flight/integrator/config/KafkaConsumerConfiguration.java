package com.tiket.tix.dummy.flight.integrator.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "kafka.listener.dummy-itinerary")
@EnableKafka
@Getter
@Setter
public class KafkaConsumerConfiguration {

  private String bootstrapServers;
  private String consumerOffsetReset;
  private String consumerGroupId;
  private int concurrency;
  private int retryMaxAttempts;
  private long retryBackOffPeriod;
  private int maxPartitionFetchBytesConfig;

  @Bean
  public Map<String, Object> consumerConfig() {
    Map<String, Object> props = new HashMap<>();

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    // consumer groups allow a pool of processes to divide the work of
    // consuming and processing records
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerOffsetReset);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
    props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytesConfig);

    return props;
  }

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfig());
  }

  @Bean("kafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setConcurrency(concurrency);

    return factory;
  }
}