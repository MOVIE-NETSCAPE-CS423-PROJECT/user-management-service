package com.movienetscape.usermanagementservice.model;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVerification {

    @Id
    @Column(unique = true)
    private String id;

    @Column(unique = true)
    private String token;

    @Column(unique = true)
    private LocalDateTime tokenExpirationTime;

    private boolean verified;

    @Column(unique = true, nullable = false)
    @JoinColumn(name = "user_id")
    private User user;


}
