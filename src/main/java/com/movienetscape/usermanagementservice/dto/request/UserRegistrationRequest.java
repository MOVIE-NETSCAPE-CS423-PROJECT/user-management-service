package com.movienetscape.usermanagementservice.dto.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.movienetscape.usermanagementservice.dto.response.Plan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegistrationRequest {

    @NotBlank(message = "Firstname can't be blank")
    private String firstname;

    @NotBlank(message = "Lastname can't be blank")
    private String lastname;

    @NotBlank(message = "Email can't be blank")
    @Pattern(
            regexp = "^[a-zA-Z0-9.]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email address format"
    )
    @Pattern(
            regexp = "^[a-zA-Z0-9.]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email address format"
    )
    @Email
    private String email;

    private AddressDto addressDto;

    @NotBlank(message = "Password can't be blank")
    private String password;

    @NotNull(message = "UserSelectedPlan can't be null")
    private Plan userSelectedPlan;


    @Pattern(
            regexp = "^(https?:\\/\\/)[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+(\\/[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]*)?$",
            message = "Invalid URL format"
    )

    private String profileImageUrl;


}
