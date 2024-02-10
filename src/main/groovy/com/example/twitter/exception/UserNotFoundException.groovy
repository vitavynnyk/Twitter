package com.example.twitter.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UserNotFoundException extends ResponseStatusException {
    UserNotFoundException() {
        super(HttpStatus.NOT_FOUND)
    }
}
