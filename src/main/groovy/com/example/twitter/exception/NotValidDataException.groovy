package com.example.twitter.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class NotValidDataException extends ResponseStatusException {
    NotValidDataException() {
        super(HttpStatus.BAD_REQUEST) }
}
