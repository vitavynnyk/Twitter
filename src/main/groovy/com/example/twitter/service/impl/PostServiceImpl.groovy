package com.example.twitter.service.impl

import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.exception.PostNotFoundException
import com.example.twitter.exception.UserNotFoundException
import com.example.twitter.mapper.PostMapper
import com.example.twitter.model.User
import com.example.twitter.repository.PostRepository
import com.example.twitter.repository.UserRepository
import com.example.twitter.service.PostService
import com.example.twitter.util.SecurityUtil
import com.example.twitter.util.SuccessResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service


@Service
class PostServiceImpl implements PostService {

    private PostRepository postRepository
    private PostMapper postMapper
    private SecurityUtil securityUtil
    private UserRepository userRepository

    PostServiceImpl(PostRepository postRepository, PostMapper postMapper, SecurityUtil securityUtil, UserRepository userRepository) {
        this.postRepository = postRepository
        this.postMapper = postMapper
        this.securityUtil = securityUtil
        this.userRepository = userRepository
    }

    @Override
    PostResponseDto create(User user, PostRequestDto postRequestDto) {
        var createdPost = postRepository.save(postMapper.toModel(user, postRequestDto))
        user.getOwnPosts().add(createdPost)
        userRepository.save(user)
        return postMapper.toDto(createdPost)
    }

    @Override
    PostResponseDto update(String id, PostRequestDto postRequestDto) {
        var post = postRepository.findById(id)
        post.ifPresentOrElse({ p ->
            if (p.user.id == securityUtil.getCurrentUserId()) {
                p.setContent(postRequestDto.content())
                postRepository.save(p)
            } else {
                throw new AccessDeniedException("Post does not belong to user")
            }
        }, { -> throw new PostNotFoundException() })
        return postMapper.toDto(post.get())
    }

    @Override
    String delete(String id) {
        var post = postRepository.findById(id)
        post.ifPresentOrElse({ p ->
            if (p.user.id == securityUtil.getCurrentUserId()) {
                postRepository.deleteById(id)
            } else {
                throw new AccessDeniedException("Post does not belong to user")
            }
        }, { return new SuccessResponse("Post does not exist") })

        return new SuccessResponse("Post was deleted successfully")
    }
}

