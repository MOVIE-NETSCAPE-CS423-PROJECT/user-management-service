package com.movienetscape.usermanagementservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @NotBlank
    private String email;

    private String street;

    private String city;

    private String state;

    private String zip;

    private String profileImageUrl;


}
