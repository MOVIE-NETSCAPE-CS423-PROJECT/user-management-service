package com.movienetscape.usermanagementservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    @NotBlank(message = "Street can't be blank")
    private String street;
    @NotBlank(message = "City can't be blank")
    private String city;
    @NotBlank(message = "State can't be blank")
    private String state;
    @NotBlank(message = "Zip can't be blank")
    @NotBlank(message = "Zip code can't be blank")
    @Pattern(
            regexp = "^[0-9]{5}(?:-[0-9]{4})?$",
            message = "Invalid zip code format"
    )
    private String zip;
}
