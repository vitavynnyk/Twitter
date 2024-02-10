package com.example.twitter.dto.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

record PostRequestDto(

        @Size(min = 1, max = 400)
                String content

) {

}