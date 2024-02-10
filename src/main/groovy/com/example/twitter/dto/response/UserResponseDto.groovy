package com.example.twitter.dto.response

import com.example.twitter.model.enums.AuthorityType

import javax.validation.constraints.Size


record UserResponseDto(
        String id,
        String nikName,
        Set<AuthorityType> roles

) {

}