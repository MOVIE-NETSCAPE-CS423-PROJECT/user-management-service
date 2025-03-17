package com.movienetscape.usermanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@Validated
public class UserManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserManagementServiceApplication.class, args);
	}

}
