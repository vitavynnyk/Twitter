package com.example.twitter.mapper

import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.dto.response.UserResponseDto
import com.example.twitter.model.Post
import com.example.twitter.model.User
import org.springframework.security.core.annotation.AuthenticationPrincipal

import org.springframework.stereotype.Component

import java.time.Instant

@Component
class PostMapper {

    PostResponseDto toDto(Post post) {
        return new PostResponseDto(
                id: post.id,
                content: post.content,
                creatingDate: String.valueOf(post.creatingDate),
                nikName: post.user.nikName)

    }

    Post toModel(User user, PostRequestDto postRequestDto) {
        return new Post(
                content: postRequestDto.content(),
                creatingDate: Instant.now(),
                user: user)
    }
}
