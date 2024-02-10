package com.example.twitter.controller

import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.model.User
import com.example.twitter.service.PostService
import com.example.twitter.service.UserService
import com.example.twitter.util.SecurityUtil
import jakarta.servlet.http.HttpServletRequest

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.annotation.CurrentSecurityContext
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/posts")
class PostController {
    private PostService postService
    private UserService userService


    PostController(PostService postService, UserService userService) {
        this.postService = postService
        this.userService = userService
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    PostResponseDto createPost(@CurrentSecurityContext(expression = "authentication")
                                       Authentication authentication, @RequestBody PostRequestDto postRequestDto) {
        var user = userService.findById(authentication.getName())
        return postService.create(user, postRequestDto)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
// ToDo поменять возвращаемій тип
    String deletePost(@PathVariable("id") String id) {
        return postService.delete(id)
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")

    String editePost(@PathVariable("id") String id, @RequestBody PostRequestDto postRequestDto) {
        return postService.update(id, postRequestDto)
    }

}
