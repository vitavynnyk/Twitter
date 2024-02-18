package com.example.twitter.service.impl

import com.example.twitter.dto.request.CommentRequestDto
import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.response.CommentResponseDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.exception.PostNotFoundException
import com.example.twitter.mapper.Mapper
import com.example.twitter.model.User
import com.example.twitter.repository.CommentRepository
import com.example.twitter.repository.LikeRepository
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
    private Mapper mapper
    private SecurityUtil securityUtil
    private UserRepository userRepository
    private CommentRepository commentRepository
    private LikeRepository likeRepository


    PostServiceImpl(PostRepository postRepository, Mapper mapper, SecurityUtil securityUtil,
                    UserRepository userRepository, CommentRepository commentRepository,
                    LikeRepository likeRepository) {
        this.postRepository = postRepository
        this.mapper = mapper
        this.securityUtil = securityUtil
        this.userRepository = userRepository
        this.commentRepository = commentRepository
        this.likeRepository = likeRepository
    }

    @Override
    PostResponseDto create(User user, PostRequestDto postRequestDto) {
        def createdPost = postRepository.save(mapper.toModel(user, postRequestDto))
        println createdPost
        user.getOwnPosts().add(createdPost)
        userRepository.save(user)
        return mapper.toDto(createdPost)
    }

    @Override
    PostResponseDto update(String id, PostRequestDto postRequestDto) {
        def post = postRepository.findById(id)
        post.ifPresentOrElse({
            if (it.user.username == securityUtil.getCurrentUserName()) {
                it.setContent(postRequestDto.content())
                postRepository.save(it)
            } else {
                throw new AccessDeniedException("Post does not belong to user")
            }
        }, { -> throw new PostNotFoundException() })
        return mapper.toDto(post.get())
    }

    @Override
    SuccessResponse delete(String id) {
        def post = postRepository.findById(id)

        if (post.isPresent()) {
            def currentUser = userRepository.findByUsername(securityUtil.getCurrentUserName())
            if (post.get().user.username == securityUtil.getCurrentUserName()) {
                currentUser.get().getOwnPosts().remove(post)
                userRepository.save(currentUser.get())
                likeRepository.deleteByPostId(id)
                commentRepository.deleteByPostId(id)
                postRepository.deleteById(id)

            } else {
                throw new AccessDeniedException("Post does not belong to user")
            }
        } else {
            return new SuccessResponse("Post does not exist")
        }
        return new SuccessResponse("Post was deleted successfully")
    }

    @Override
    SuccessResponse commentPost(User user, CommentRequestDto commentDto, String postId) {
        def post = postRepository.findById(postId)
        if (post.isPresent()) {
            def createdComment = commentRepository.save(mapper.toModel(user, commentDto, post.get()))
            post.get().getComments().add(createdComment)
            postRepository.save(post.get())
            return new SuccessResponse("Comment was added successfully")
        } else {
            return new SuccessResponse("Post is not found")
        }
    }

    @Override
    List<CommentResponseDto> getAllComments(String postId) {
        def post = postRepository.findById(postId)
        if (post.isPresent()) {
            return post.get().getComments().stream()
                    .map(comment -> mapper.toDto(comment))
                    .toList()
        } else {
            throw new PostNotFoundException()
        }
    }

    @Override
    SuccessResponse leaveLike(User user, String postId) {
        def post = postRepository.findById(postId)
        if (post.isPresent()) {
            def isAlreadyLiked = post.get().getLikes().stream()
                    .filter { it.user.id == user.id }
                    .findFirst().isPresent()
            if (isAlreadyLiked) {
                return new SuccessResponse("You have already liked this post ")
            } else {
                def like = likeRepository.save(mapper.toModel(user, post.get()))
                post.get().getLikes().add(like)
                postRepository.save(post.get())
                return new SuccessResponse("Like was added successfully")
            }
        } else {
            return new SuccessResponse("Post is not found")
        }
    }

    @Override
    SuccessResponse removeLike(User user, String postId) {
        def post = postRepository.findById(postId)
        if (post.isPresent()) {
            def like = likeRepository.findByUser(user)

            return like.map {
                post.get().getLikes().remove(it)
                likeRepository.delete(it)
                postRepository.save(post.get())
                new SuccessResponse("Like was deleted successfully")
            }.orElse(new SuccessResponse("User did not leave like here"))
        } else {
            return new SuccessResponse("Post is not found")
        }
    }

    @Override
    SuccessResponse addToFavorite(User user, String postId) {
        def post = postRepository.findById(postId)
        def favoritePosts = user.getFavoritePosts()
        if (post.isPresent()) {
            if (favoritePosts.contains(post.get())) {
                return new SuccessResponse("This post is already in favorites")
            } else {
                favoritePosts.add(post.get())
                userRepository.save(user)
                return new SuccessResponse("Post was added to favorite successfully")
            }
        } else {
            return new SuccessResponse("Post is not found")
        }
    }

    @Override
    SuccessResponse removeFromFavorite(User user, String postId) {
        def post = postRepository.findById(postId)
        if (post.isPresent()) {
            user.getFavoritePosts().remove(post.get())
            userRepository.save(user)
            return new SuccessResponse("Post was deleted from favorite successfully")
        } else {
            return new SuccessResponse("Post is not found")
        }
    }
}
