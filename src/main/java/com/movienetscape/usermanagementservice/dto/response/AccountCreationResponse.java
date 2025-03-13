package com.movienetscape.usermanagementservice.dto.response;


import com.movienetscape.usermanagementservice.dto.request.AccountCreationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreationResponse {
    private String accountId;
    private String message;
}
