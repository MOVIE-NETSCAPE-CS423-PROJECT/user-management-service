package com.movienetscape.usermanagementservice.service.contract;

import com.movienetscape.usermanagementservice.dto.request.UpdateUserRequest;
import com.movienetscape.usermanagementservice.dto.request.UserRegistrationRequest;
import com.movienetscape.usermanagementservice.dto.response.SimpleMessageResponse;
import com.movienetscape.usermanagementservice.dto.response.UpdateUserResponse;
import com.movienetscape.usermanagementservice.dto.response.UserRegistrationResponse;
import reactor.core.publisher.Mono;

public interface UserService {

    SimpleMessageResponse verifyUser(String token);

    Mono<UserRegistrationResponse> registerUser(UserRegistrationRequest request);

    Mono<UpdateUserResponse> updateUser(UpdateUserRequest request);





}
