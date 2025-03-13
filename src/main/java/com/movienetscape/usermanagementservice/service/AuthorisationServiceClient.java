package com.movienetscape.usermanagementservice.service;

import com.movienetscape.usermanagementservice.dto.request.UserCredentialRegistrationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthorisationServiceClient {

    private final WebClient authServiceClient;

    public AuthorisationServiceClient(WebClient.Builder webClientBuilder) {
        this.authServiceClient = webClientBuilder.baseUrl("http://localhost:8081/api/v1/auth").build();
    }

    public Mono<Void> registerUserCredentials(UserCredentialRegistrationRequest request) {
        return authServiceClient.post()
                .uri("/create")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
