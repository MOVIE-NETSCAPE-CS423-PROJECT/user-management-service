package com.movienetscape.usermanagementservice.service.contract;

import com.movienetscape.usermanagementservice.dto.request.UserRegistrationRequest;
import com.movienetscape.usermanagementservice.dto.response.SuccessResponse;
import com.movienetscape.usermanagementservice.dto.response.SimpleMessageResponse;
import reactor.core.publisher.Mono;

public interface UserService {

    SimpleMessageResponse verifyUser(String token);

    Mono<SuccessResponse> registerUser(UserRegistrationRequest request);

    Mono<SuccessResponse> updateUser(UserRegistrationRequest request);


}
