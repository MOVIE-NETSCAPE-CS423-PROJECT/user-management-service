package com.movienetscape.usermanagementservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", indexes = @Index(name = "idx_user_id", columnList = "email"))
public class User {

    @Id
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(name = "account_id", unique = true)
    private String accountId;

    @Embedded
    private Address address;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean verified;

    @Column
    private String profileImageUrl;


    public void activateAccount() {
        this.active = true;
    }

    public void deactivateAccount() {
        this.active = false;
    }

    public boolean hasAccount() {
        return this.accountId != null && !this.accountId.isEmpty();
    }


    public void linkAccount(String accountId) {
        if (this.hasAccount()) {
            throw new IllegalStateException("User already has an associated account.");
        }
        this.accountId = accountId;
    }


    public void verifyUser() {
        this.verified = true;
    }


    public void updateUserDetails(String firstName, String lastName, Address address) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }
}
