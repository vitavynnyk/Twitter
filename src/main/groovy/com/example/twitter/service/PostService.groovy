package com.example.twitter.service

import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.model.User


interface PostService {
    PostResponseDto create(User user, PostRequestDto postRequestDto)

    PostResponseDto update(String id, PostRequestDto postRequestDto)

    String delete(String id)

}
