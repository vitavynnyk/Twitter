package com.example.twitter.mapper

import com.example.twitter.dto.request.CommentRequestDto
import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.CommentResponseDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.dto.response.UserResponseDto
import com.example.twitter.model.Comment
import com.example.twitter.model.Like
import com.example.twitter.model.Post
import com.example.twitter.model.User
import com.example.twitter.model.enums.AuthorityType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

import java.time.Instant

@Component
class Mapper {

    PasswordEncoder passwordEncoder

    Mapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder
    }

    static UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                id: user.getId(),
                nikName: user.getNikName(),
                roles: user.getRoles()
        )

    }

    User toModel(UserRequestDto userDto) {
        return new User(
                username: userDto.username(),
                password: passwordEncoder.encode(userDto.password()),
                isActive: true,
                roles: Set.of(AuthorityType.USER),
                nikName: userDto.nikName(),
                ownPosts: new HashSet<Post>(),
                subscription: new HashSet<User>(),
                favoritePost: new HashSet<Post>())

    }

    static PostResponseDto toDto(Post post) {
        return new PostResponseDto(
                id: post.id,
                content: post.content,
                creatingDate: String.valueOf(post.creatingDate),
                nikName: post.user.nikName)

    }

    static Post toModel(User user, PostRequestDto postRequestDto) {
        return new Post(
                content: postRequestDto.content(),
                creatingDate: Instant.now(),
                user: user,
                comments: new ArrayList<Comment>(),
                like: new ArrayList<Like>())
    }

    static Comment toModel(User user, CommentRequestDto commentRequestDto, Post post) {
        return new Comment(
                content: commentRequestDto.content(),
                creatingDate: Instant.now(),
                user: user,
                post: post,
        )
    }

    static CommentResponseDto toDto(Comment comment) {
        return new CommentResponseDto(
                content: comment.getContent(),
                time: String.valueOf(comment.getCreatingDate()),
                user: comment.getUser().nikName,
                userId: comment.getUser().id,
        )
    }

    static Like toModel(User user, Post post) {
        return new Like(
                user: user,
                post: post
        )
    }
}
