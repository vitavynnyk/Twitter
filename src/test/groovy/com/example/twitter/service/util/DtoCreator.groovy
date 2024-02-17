package com.example.twitter.service.util

import com.example.twitter.dto.request.CommentRequestDto
import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.CommentResponseDto
import com.example.twitter.model.Comment
import com.example.twitter.model.Like
import com.example.twitter.model.Post
import com.example.twitter.model.User
import com.example.twitter.model.enums.AuthorityType
import org.springframework.cglib.core.Local

import java.time.Instant
import java.time.LocalDateTime

class DtoCreator {

    static User createUser() {
        return new User(
                id: "userId",
                username: "Vita",
                password: "12dvv",
                isActive: true,
                roles: Set.of(AuthorityType.USER),
                nikName: "Vita123",
                ownPosts: new HashSet<Post>(),
                subscription: new HashSet<User>(),
                favoritePosts: new HashSet<Post>())
    }

    static User createUserInDataBase() {
        return new User(
                username: "Vita",
                password: "12dvv",
                isActive: true,
                roles: Set.of(AuthorityType.USER),
                nikName: "Vita333",
                ownPosts: new HashSet<Post>(),
                subscription: new HashSet<User>(),
                favoritePosts: new HashSet<Post>())
    }

    static Post createPost() {
        return new Post(id: "123",
                content: "content",
                creatingDate: Instant.now(),
                user: createUser(),
                comments: new ArrayList<Comment>(),
                likes: new ArrayList<Like>())
    }

    static Comment createComment() {
        return new Comment(
                content: "Comment",
                user: createUser(),
                post: createPost(),
                creatingDate: Instant.now()


        )
    }

    static PostRequestDto createPostRequestDto() {
        return new PostRequestDto("Content")
    }

    static PostRequestDto createPostUpdatingRequestDto() {
        return new PostRequestDto("New content")
    }


    static UserRequestDto createUserRequestDto() {
        return new UserRequestDto(
                username: "Vita",
                password: "12dvv",
                nikName: "Vita123")
    }

    static CommentRequestDto createCommentRequestDto() {
        return new CommentRequestDto(
                content: "This is comment")
    }


    static Like createLike() {
        return new Like(

//                post: createPost()
                )

    }


}
