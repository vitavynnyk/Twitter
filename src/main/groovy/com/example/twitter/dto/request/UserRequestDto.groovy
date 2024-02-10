package com.example.twitter.dto.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

record UserRequestDto(

        @Size(min = 2, max = 20)
                String username,

        @Size(min = 8, max = 30)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+")
                String password,

        @Size(min = 2, max = 20)
                String nikName

) {

}