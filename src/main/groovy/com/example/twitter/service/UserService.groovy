package com.example.twitter.service

import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.dto.response.UserResponseDto
import com.example.twitter.model.Post
import com.example.twitter.model.User
import com.example.twitter.util.SuccessResponse


interface UserService {
    String create(UserRequestDto user)

    SuccessResponse delete(String id)

    UserResponseDto update(String id, UserRequestDto userDto)

    User findById(String id)

    User findByUserId(String id)

    SuccessResponse subscribeUser(String subscriptionUserId)

    SuccessResponse unsubscribeUser(String subscriptionUserId)

    List<PostResponseDto> getOwnPosts(User user)

    List<PostResponseDto> getLikedPosts(String id)

    List<PostResponseDto> getCommentedPosts(String id)

    List<PostResponseDto> getFeed(String id)

    List<PostResponseDto> getSubscriptionFeed(User user)
}