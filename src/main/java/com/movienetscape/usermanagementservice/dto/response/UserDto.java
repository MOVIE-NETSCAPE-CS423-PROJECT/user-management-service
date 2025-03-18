package com.movienetscape.usermanagementservice.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.movienetscape.usermanagementservice.dto.request.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String firstName;
    private String lastName;
    private String userId;
    private String profileImageUrl;
    private String activePlanName;
    private AddressDto addressDto;

}
