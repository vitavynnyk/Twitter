package com.example.twitter.controller

import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.dto.response.UserResponseDto
import com.example.twitter.service.UserService
import com.example.twitter.util.SecurityUtil
import com.example.twitter.util.SuccessResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.annotation.CurrentSecurityContext
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
    @PreAuthorize("hasRole('USER')")
    SuccessResponse deleteUser(@PathVariable("id") String id) {
        return userService.delete(id)
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    UserResponseDto updateUser(@PathVariable("id") String id, @RequestBody UserRequestDto userDto) {
        return userService.update(id, userDto)
    }

    @PatchMapping("/subscribe/{id}")
    @PreAuthorize("hasRole('USER')")
    SuccessResponse subscribeUser(@PathVariable("id") String id) {
        return userService.subscribeUser(id)
    }

    @PatchMapping("/unsubscribe/{id}")
    @PreAuthorize("hasRole('USER')")
    SuccessResponse unSubscribeUser(@PathVariable("id") String id) {
        return userService.unsubscribeUser(id)
    }

    @GetMapping("/my-posts")
    @PreAuthorize("hasRole('USER')")
    List<PostResponseDto> getOwnPosts(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        def user = userService.findById(authentication.getName())
        return userService.getOwnPosts(user)
    }

    @GetMapping("/my-likes")
    @PreAuthorize("hasRole('USER')")
    List<PostResponseDto> getOwnLikedPosts(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        return userService.getLikedPosts(authentication.getName())
    }

    @GetMapping("/my-comments")
    @PreAuthorize("hasRole('USER')")
    List<PostResponseDto> getOwnComments(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        return userService.getCommentedPosts(authentication.getName())
    }


    @GetMapping("/{id}/posts")
    @PreAuthorize("hasRole('USER')")
    List<PostResponseDto> getOtherUsersPosts(@PathVariable("id") String id) {
        def user = userService.findById(id)
        return userService.getOwnPosts(user)
    }

    @GetMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    List<PostResponseDto> getOtherUsersLikedPosts(@PathVariable("id") String id) {
        return userService.getLikedPosts(id)
    }

    @GetMapping("/{id}/comments")
    @PreAuthorize("hasRole('USER')")
    List<PostResponseDto> getOtherUsersCommentedPosts(@PathVariable("id") String id) {
        return userService.getCommentedPosts(id)
    }

    @GetMapping("/{id}/feed")
    @PreAuthorize("hasRole('USER')")
    List<PostResponseDto> getOtherUsersFeed(@PathVariable("id") String id) {
        return userService.getFeed(id)
    }

    @GetMapping("/feed")
    @PreAuthorize("hasRole('USER')")
    List<PostResponseDto> getMyFeed(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        return userService.getFeed(authentication.getName())
    }

    @GetMapping("/subscriptions-feed")
    @PreAuthorize("hasRole('USER')")
    List<PostResponseDto> getSubscriptionsFeed(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        def user = userService.findById(authentication.getName())
        return userService.getSubscriptionFeed(user)
    }
}
