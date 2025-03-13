package com.movienetscape.usermanagementservice.service.impl;


import com.movienetscape.usermanagementservice.dto.request.AccountCreationRequest;
import com.movienetscape.usermanagementservice.dto.response.AccountCreationResponse;
import com.movienetscape.usermanagementservice.dto.response.ErrorResponse;
import com.movienetscape.usermanagementservice.util.exception.UserAccountCreationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceClient {

    private final WebClient accountServiceWebClient;

    public AccountServiceClient(WebClient.Builder accountServiceWebClient) {
        this.accountServiceWebClient = accountServiceWebClient.baseUrl("http://localhost:8085").build();

    }

    public Mono<AccountCreationResponse> createAccount(String email) {
        return accountServiceWebClient.post()
                .uri("/api/v1/accounts")
                .bodyValue(new AccountCreationRequest(email))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(ErrorResponse.class)
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

}
