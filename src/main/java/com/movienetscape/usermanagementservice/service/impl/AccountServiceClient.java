package com.movienetscape.usermanagementservice.service.impl;


import com.movienetscape.usermanagementservice.dto.request.AccountCreationRequest;
import com.movienetscape.usermanagementservice.dto.request.AddressDto;
import com.movienetscape.usermanagementservice.dto.response.AccountCreationResponse;
import com.movienetscape.usermanagementservice.dto.response.Plan;
import com.movienetscape.usermanagementservice.dto.response.SimpleMessageResponse;
import com.movienetscape.usermanagementservice.dto.response.UserDto;
import com.movienetscape.usermanagementservice.messaging.event.UpdatedUserEvent;
import com.movienetscape.usermanagementservice.messaging.producer.KafkaEventProducerService;
import com.movienetscape.usermanagementservice.util.exception.UserAccountCreationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceClient {

    private final WebClient accountServiceWebClient;

    private final KafkaEventProducerService kafkaEventProducer;

    public AccountServiceClient(WebClient.Builder accountServiceWebClient, KafkaEventProducerService kafkaEventProducer) {
        this.accountServiceWebClient = accountServiceWebClient.baseUrl("http://localhost:8085").build();
        this.kafkaEventProducer = kafkaEventProducer;

    }

    public Mono<AccountCreationResponse> createAccount(String email, String firstname, String lastname, AddressDto address, String profileImageUrl, Plan selectedPlan) {
        return accountServiceWebClient.post()
                .uri("/api/v1/accounts")
                .bodyValue(new AccountCreationRequest(email, firstname, lastname, profileImageUrl, address, selectedPlan))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(SimpleMessageResponse.class)
                                .flatMap(error -> Mono.error(new UserAccountCreationException(error.getMessage())))
                )
                .bodyToMono(AccountCreationResponse.class);
    }


    public Mono<Void> deleteAccount(String accountId) {
        return accountServiceWebClient.delete()
                .uri("api/v1/accounts/{accountId}", accountId)
                .retrieve()
                .bodyToMono(Void.class);
    }


    public void publishUpdatedUser(UserDto userDto) {

        kafkaEventProducer.publishUpdatedUserEvent(
                UpdatedUserEvent.builder()
                        .state(userDto.getAddressDto().getState())
                        .city(userDto.getAddressDto().getCity())
                        .street(userDto.getAddressDto().getStreet())
                        .zip(userDto.getAddressDto().getZip())
                        .lastName(userDto.getLastName())
                        .firstName(userDto.getFirstName())
                        .profileImageUrl(userDto.getProfileImageUrl())
                        .userId(userDto.getUserId())
                        .build()
        );
    }

}
