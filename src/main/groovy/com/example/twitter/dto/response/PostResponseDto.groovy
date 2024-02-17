package com.example.twitter.dto.response

record PostResponseDto(
        String id,
        String content,
        String creatingDate,
        String nikName,
        String authorId,
        List<CommentResponseDto> comments,
        List<String> likesByUsers

) {

}