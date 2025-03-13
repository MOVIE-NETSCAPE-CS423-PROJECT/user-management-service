package com.movienetscape.usermanagementservice.config;


import com.movienetscape.usermanagementservice.messaging.event.UserVerifiedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaProducerConfig {


    @Bean
    public NewTopic userVerifiedTopic() {
        return new NewTopic("user-verified-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic userRegisteredTopic() {
        return new NewTopic("user-registered-topic", 1, (short) 1);
    }

    @Bean
    public ProducerFactory<String, UserVerifiedEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        config.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);

    }

    @Bean
    public KafkaTemplate<String, UserVerifiedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
