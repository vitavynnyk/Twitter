package com.example.twitter.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class PostNotFoundException extends ResponseStatusException {
    PostNotFoundException() {
        super(HttpStatus.NOT_FOUND)
    }
}
