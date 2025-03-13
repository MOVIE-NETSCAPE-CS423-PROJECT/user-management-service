package com.movienetscape.usermanagementservice.dto.response;


import com.movienetscape.usermanagementservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Immutable
public class UserRegistrationResponse {

    private String message;
    private User user;

}
