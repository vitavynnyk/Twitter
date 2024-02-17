package com.example.twitter.controller

import com.example.twitter.dto.request.CommentRequestDto
import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.response.CommentResponseDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.service.PostService
import com.example.twitter.service.UserService
import com.example.twitter.util.SuccessResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.CurrentSecurityContext
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

// TODO описания к методам
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
    PostResponseDto createPost(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                               @RequestBody PostRequestDto postRequestDto) {
        println authentication.name
        def user = userService.findById(authentication.getName())
        return postService.create(user, postRequestDto)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    SuccessResponse deletePost(@PathVariable("id") String id) {
        return postService.delete(id)
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    PostResponseDto editePost(@PathVariable("id") String id, @RequestBody PostRequestDto postRequestDto) {
        return postService.update(id, postRequestDto)
    }

    @PatchMapping("/{id}/comment")
    @PreAuthorize("hasRole('USER')")
    SuccessResponse commentPost(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                       @PathVariable("id") String id,
                       @RequestBody CommentRequestDto commentRequestDto) {
        def user = userService.findById(authentication.getName())
        return postService.commentPost(user, commentRequestDto, id)
    }

    @GetMapping("/{id}/comments")
    @PreAuthorize("hasRole('USER')")
    List<CommentResponseDto> getAllCommentsByPost(@PathVariable("id") String id) {
        return postService.getAllComments(id)
    }

    @PatchMapping("/{id}/unlike")
    @PreAuthorize("hasRole('USER')")
    SuccessResponse unlikePost(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                    @PathVariable("id") String id) {
        def user = userService.findById(authentication.getName())
        return postService.removeLike(user, id)
    }

    @PatchMapping("/{id}/like")
    @PreAuthorize("hasRole('USER')")
    SuccessResponse likePost(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                      @PathVariable("id") String id) {
        def user = userService.findById(authentication.getName())
        return postService.leaveLike(user, id)
    }

    @PatchMapping("/{id}/favorite")
    @PreAuthorize("hasRole('USER')")
    SuccessResponse addToFavorite(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                    @PathVariable("id") String id) {
        def user = userService.findById(authentication.getName())
        return postService.addToFavorite(user, id)
    }
    @PatchMapping("/{id}/delete-from-favorite")
    @PreAuthorize("hasRole('USER')")
    SuccessResponse removeFromFavorite(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                         @PathVariable("id") String id) {
        def user = userService.findById(authentication.getName())
        return postService.removeFromFavorite(user, id)
    }
}
