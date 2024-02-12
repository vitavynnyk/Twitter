package com.example.twitter.dto.response

import javax.validation.constraints.Size

record CommentResponseDto(

        @Size(min = 1, max = 400) String content,
        String time,
        String user,
        String userId) {

}