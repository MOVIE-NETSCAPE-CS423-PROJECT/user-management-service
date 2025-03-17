package com.movienetscape.usermanagementservice.dto.response;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserResponse {


    private String firstname;

    private String lastname;

    private String email;

    private String street;

    private String city;

    private String state;

    private String zip;

    private String profileImageUrl;


}
