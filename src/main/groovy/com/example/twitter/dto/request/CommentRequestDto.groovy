package com.example.twitter.dto.request

import javax.validation.constraints.Size

record CommentRequestDto(@Size(min = 1, max = 400)
                                 String content) {

}