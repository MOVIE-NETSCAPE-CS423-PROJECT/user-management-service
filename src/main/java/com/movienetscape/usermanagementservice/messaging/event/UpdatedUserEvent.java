package com.movienetscape.usermanagementservice.messaging.event;


import com.movienetscape.usermanagementservice.dto.request.AddressDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedUserEvent {

    private String firstName;
    private String lastName;
    private String userId;
    private String profileImageUrl;
    private String activePlanName;
    private String street;
    private String city;
    private String state;
    private String zip;
}
