package com.example.twitter.service

import com.example.twitter.dto.request.CommentRequestDto
import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.response.CommentResponseDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.model.Comment
import com.example.twitter.model.User
import com.example.twitter.util.SuccessResponse


interface PostService {
    PostResponseDto create(User user, PostRequestDto postRequestDto)

    PostResponseDto update(String id, PostRequestDto postRequestDto)

    SuccessResponse delete(String id)

    SuccessResponse commentPost(User user, CommentRequestDto content, String postId)

    List<CommentResponseDto> getAllComments(String postId)

    SuccessResponse leaveLike(User user,  String postId)

    SuccessResponse removeLike(User user,  String postId)

    SuccessResponse addToFavorite(User user, String postId)

    SuccessResponse removeFromFavorite(User user, String postId)

}
