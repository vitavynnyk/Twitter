package com.example.twitter.controller

import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.dto.response.UserResponseDto
import com.example.twitter.service.UserService
import com.example.twitter.util.SuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.CurrentSecurityContext
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller")
class UserController {
    private UserService userService

    UserController(UserService userService) {
        this.userService = userService
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    String createUser(@RequestBody UserRequestDto userDto) {
        return userService.create(userDto)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete user")
    SuccessResponse deleteUser(@PathVariable("id") String id) {
        return userService.delete(id)
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update user")
    UserResponseDto updateUser(@PathVariable("id") String id, @RequestBody UserRequestDto userDto) {
        return userService.update(id, userDto)
    }

    @PatchMapping("/subscribe/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Subscribe user")
    SuccessResponse subscribeUser(@PathVariable("id") String id) {
        return userService.subscribeUser(id)
    }

    @PatchMapping("/unsubscribe/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Unsubscribe user")
    SuccessResponse unSubscribeUser(@PathVariable("id") String id) {
        return userService.unsubscribeUser(id)
    }

    @GetMapping("/my-posts")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get own posts")
    List<PostResponseDto> getOwnPosts(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        def user = userService.findById(authentication.getName())
        return userService.getOwnPosts(user)
    }

    @GetMapping("/my-likes")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get own liked posts")
    List<PostResponseDto> getOwnLikedPosts(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        return userService.getLikedPosts(authentication.getName())
    }

    @GetMapping("/my-comments")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get own commented posts")
    List<PostResponseDto> getOwnComments(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        return userService.getCommentedPosts(authentication.getName())
    }

    @GetMapping("/{id}/posts")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get other user`s posts")
    List<PostResponseDto> getOtherUsersPosts(@PathVariable("id") String id) {
        def user = userService.findByUserId(id)
        return userService.getOwnPosts(user)
    }

    @GetMapping("/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get other user`s liked posts")
    List<PostResponseDto> getOtherUsersLikedPosts(@PathVariable("id") String id) {
        return userService.getLikedPosts(id)
    }

    @GetMapping("/{id}/comments")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get other user`s commented posts")
    List<PostResponseDto> getOtherUsersCommentedPosts(@PathVariable("id") String id) {
        return userService.getCommentedPosts(id)
    }

    @GetMapping("/{id}/feed")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get other user`s feed")
    List<PostResponseDto> getOtherUsersFeed(@PathVariable("id") String id) {
        return userService.getFeed(id)
    }

    @GetMapping("/feed")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get own feed")
    List<PostResponseDto> getMyFeed(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        def user = userService.findById(authentication.getName())
        return userService.getFeed(user.id)
    }

    @GetMapping("/subscriptions-feed")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get subscription`s feed")
    List<PostResponseDto> getSubscriptionsFeed(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        def user = userService.findById(authentication.getName())
        return userService.getSubscriptionFeed(user)
    }
}
