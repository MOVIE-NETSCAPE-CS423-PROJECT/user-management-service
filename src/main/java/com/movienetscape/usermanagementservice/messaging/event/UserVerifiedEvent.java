package com.movienetscape.usermanagementservice.messaging.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVerifiedEvent {

    private String userId;

    private boolean verified;

}
