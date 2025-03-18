package com.movienetscape.usermanagementservice.dto.request;


import com.movienetscape.usermanagementservice.dto.response.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreationRequest {


    private String userId;

    private String firstName;

    private String lastName;

    private String profileImageUrl;

    private AddressDto addressDto;

    private Plan userSelectedPlan;


}
