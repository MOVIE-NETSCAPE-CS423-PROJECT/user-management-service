package com.movienetscape.usermanagementservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    private String planName;
    private int maxMoviesPerProfileOnActiveSubscription;
    private int maxMoviesPerProfileOnNotActiveSubscription;
}
