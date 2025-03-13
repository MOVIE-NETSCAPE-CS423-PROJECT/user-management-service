package com.movienetscape.usermanagementservice.dto.response;


import com.movienetscape.usermanagementservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationResponse {

    private String message;
    private User user;

}
