package com.movienetscape.usermanagementservice.dto.request;


import com.movienetscape.usermanagementservice.dto.response.Plan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {

    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @NotBlank(message = "Email can't be blank")
    @Email(message = "Invalid email address format")
    private String email;
    @NotBlank
    private String password;
    @NotNull
    private Plan userSelectedPlan;


}
