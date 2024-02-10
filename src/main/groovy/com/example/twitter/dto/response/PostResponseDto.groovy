package com.example.twitter.dto.response

import com.example.twitter.model.enums.AuthorityType

record PostResponseDto(
        String id,
        String content,
        String creatingDate,
        String nikName

) {

}