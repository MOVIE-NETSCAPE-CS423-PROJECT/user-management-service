package com.movienetscape.usermanagementservice.service.contract;

import com.movienetscape.usermanagementservice.dto.request.UserRegistrationRequest;
import com.movienetscape.usermanagementservice.dto.response.UserCreationResponse;
import com.movienetscape.usermanagementservice.dto.response.UserRegistrationResponse;
import reactor.core.publisher.Mono;

public interface UserService {

   void verifyUser(String token);

    Mono<UserRegistrationResponse> registerUser(UserRegistrationRequest request);

  Mono<UserCreationResponse> getUserByEmail(String email);
}
