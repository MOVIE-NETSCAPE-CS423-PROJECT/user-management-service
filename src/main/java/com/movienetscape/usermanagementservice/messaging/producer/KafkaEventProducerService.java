package com.movienetscape.usermanagementservice.messaging.producer;

import com.movienetscape.usermanagementservice.messaging.event.UserVerifiedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KafkaEventProducerService {

    private KafkaTemplate<String, UserVerifiedEvent> kafkaTemplate;

    public void publishUserVerifiedEvent(UserVerifiedEvent userVerifiedEvent) {
        kafkaTemplate.send("User-Verified-Event", userVerifiedEvent);
    }
}
