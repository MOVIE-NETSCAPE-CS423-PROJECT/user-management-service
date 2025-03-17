package com.movienetscape.usermanagementservice.repository;


import com.movienetscape.usermanagementservice.model.User;
import com.movienetscape.usermanagementservice.model.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationRepository extends JpaRepository<UserVerification, String> {

    Optional<UserVerification> findByToken(String token);

    List<UserVerification> findUserVerificationByUser(User user);
}
