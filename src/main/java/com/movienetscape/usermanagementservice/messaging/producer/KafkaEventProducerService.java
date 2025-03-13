package com.movienetscape.usermanagementservice.messaging.producer;

import com.movienetscape.usermanagementservice.messaging.event.UserRegisteredEvent;
import com.movienetscape.usermanagementservice.messaging.event.UserVerifiedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KafkaEventProducerService {

    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserVerifiedEvent(UserVerifiedEvent userVerifiedEvent) {
        kafkaTemplate.send("User-Verified-Event", userVerifiedEvent);
    }

    public void publishUserRegisteredEvent(UserRegisteredEvent userRegisteredEvent) {
        kafkaTemplate.send("User-Verified-Event", userRegisteredEvent);
    }
}
