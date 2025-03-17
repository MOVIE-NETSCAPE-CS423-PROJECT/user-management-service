package com.movienetscape.usermanagementservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ErrorResponse  {

   private SimpleMessageResponse response;

}
