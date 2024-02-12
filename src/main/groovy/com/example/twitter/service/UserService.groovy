package com.example.twitter.service

import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.UserResponseDto
import com.example.twitter.model.User
import com.example.twitter.util.SuccessResponse


interface UserService {
    String create(UserRequestDto user)

    SuccessResponse delete(String id)

    void update(String id, UserRequestDto userDto)

    User findById(String id)

    SuccessResponse subscribeUser(String subscriptionUserId)

    SuccessResponse unsubscribeUser(String subscriptionUserId)




}