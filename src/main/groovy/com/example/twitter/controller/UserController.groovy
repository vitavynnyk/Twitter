package com.example.twitter.controller

import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.UserResponseDto
import com.example.twitter.service.UserService
import com.example.twitter.util.SecurityUtil
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/users")
class UserController {
    private UserService userService

    UserController(UserService userService) {
        this.userService = userService

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    String createUser(@RequestBody UserRequestDto userDto) {
        return userService.create(userDto)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")// ToDo поменять возвращаемій тип
    String deleteUser(@PathVariable("id") String id) {
        return userService.delete(id)
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")// ToDo поменять возвращаемій тип
    String updateUser(@PathVariable("id") String id, @RequestBody UserRequestDto userDto) {
        return userService.update(id, userDto)
    }


//    @GetMapping("/{email}")
//    UserResponseDto getUserByEmail(@PathVariable("email") String email) {
//        println userService.findByEmail(email).toString()
//        return userService.findByEmail(email)
//    }
}
