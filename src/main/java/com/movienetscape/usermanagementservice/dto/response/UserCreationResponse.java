package com.movienetscape.usermanagementservice.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.movienetscape.usermanagementservice.model.AccountInfo;
import com.movienetscape.usermanagementservice.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreationResponse {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Address address;


}
