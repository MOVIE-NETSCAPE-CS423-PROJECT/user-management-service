package com.movienetscape.usermanagementservice.messaging.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movienetscape.usermanagementservice.messaging.event.UpdatedUserEvent;
import com.movienetscape.usermanagementservice.messaging.event.UserRegisteredEvent;
import com.movienetscape.usermanagementservice.messaging.event.UserVerifiedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaEventProducerService {


    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;



    public void publishUserVerifiedEvent(UserVerifiedEvent userVerifiedEvent) {
        try {
            String eventJson = objectMapper.writeValueAsString(userVerifiedEvent);
            kafkaTemplate.send("user-verified-topic", eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize user verified event", e);
        }
    }

    public void publishUserRegisteredEvent(UserRegisteredEvent userRegisteredEvent) {
        try {
            String eventJson = objectMapper.writeValueAsString(userRegisteredEvent);
            kafkaTemplate.send("user-registered-topic", eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize user registered event", e);
        }
    }



    public void publishUpdatedUserEvent(UpdatedUserEvent updatedUserEvent) {
        try {
            String eventJson = objectMapper.writeValueAsString(updatedUserEvent);
            kafkaTemplate.send("user-updated-topic", eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize user updated event", e);
        }
    }

}
