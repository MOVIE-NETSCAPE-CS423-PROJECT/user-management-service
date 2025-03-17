package com.movienetscape.usermanagementservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserVerification {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime tokenExpirationTime;


    @Column(nullable = false)
    private boolean verified;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
