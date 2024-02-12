package com.example.twitter.service.impl

import com.example.twitter.dto.request.CommentRequestDto
import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.response.CommentResponseDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.exception.PostNotFoundException
import com.example.twitter.mapper.Mapper
import com.example.twitter.model.Comment
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
import org.springframework.transaction.annotation.Transactional

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
    @Transactional
    PostResponseDto create(User user, PostRequestDto postRequestDto) {
        def createdPost = postRepository.save(mapper.toModel(user, postRequestDto))
        user.getOwnPosts().add(createdPost)
//        userRepository.findAllBySubscription(user).each { subscription ->
//            subscription.getReceivedPosts().add(createdPost)
//            userRepository.save(subscription)
//        }
        userRepository.save(user)
        return mapper.toDto(createdPost)
    }

    @Override
    @Transactional
    PostResponseDto update(String id, PostRequestDto postRequestDto) {
        def post = postRepository.findById(id)
        post.ifPresentOrElse({ p ->
            if (p.user.id == securityUtil.getCurrentUserId()) {
                p.setContent(postRequestDto.content())
                postRepository.save(p)
            } else {
                throw new AccessDeniedException("Post does not belong to user")
            }
        }, { -> throw new PostNotFoundException() })
        return mapper.toDto(post.get())
    }

    @Override
    @Transactional
    String delete(String id) { //TODO c delete в базе разобраться
        var post = postRepository.findById(id)
        var currentUser = userRepository.findById(securityUtil.getCurrentUserId()).get()
        println userRepository.findAllBySubscription(currentUser)
        if (post.isPresent()) {
            if (post.get().user.id == securityUtil.getCurrentUserId()) {
                currentUser.getOwnPosts().remove(post)
                userRepository.save(currentUser)

//                userRepository.findAllBySubscription(currentUser).each { subscription ->
//                    subscription.getReceivedPosts().remove(post)
//                    userRepository.save(subscription)
//                }
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
    @Transactional
    List<CommentResponseDto> getAllComments(String postId) {
        return postRepository.findById(postId).get().getComments().stream()
                .map(comment -> mapper.toDto(comment))
                .toList()
    }

    @Override
    SuccessResponse leaveLike(User user, String postId) {
        def post = postRepository.findById(postId)
        if (post.isPresent()) {
            def isAlreadyLiked = post.get().getLike().stream()
                    .filter { like -> like.user.id == user.id }
                    .findFirst().isPresent()
            if (isAlreadyLiked) {
                return new SuccessResponse("You have already liked this post ")
            } else {
                def like = likeRepository.save(mapper.toModel(user, post.get()))
                post.get().getLike().add(like)
                postRepository.save(post.get())
                return new SuccessResponse("Like was added successfully")
            }
        } else {
            return new SuccessResponse("Post is not found")
        }
    }

    @Override
    SuccessResponse removeLike(User user, String postId) {//TODO удалить юзера
        def post = postRepository.findById(postId)
        if (post.isPresent()) {
            def like = likeRepository.findByPostId(postId).get()// TODO надо конкретный лайк вернуть
            post.get().getLike().remove(like)
            likeRepository.delete(like)
            postRepository.save(post.get())
            return new SuccessResponse("Like was deleted successfully")
        } else {
            return new SuccessResponse("Post is not found")
        }
    }

    @Override
    SuccessResponse addToFavorite(User user, String postId) {
        def post = postRepository.findById(postId)
        def favoritePosts = user.getFavoritePost()
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
            user.getFavoritePost().remove(post.get())
            userRepository.save(user)
            return new SuccessResponse("Post was deleted from favorite successfully")
        } else {
            return new SuccessResponse("Post is not found")
        }
    }
}
