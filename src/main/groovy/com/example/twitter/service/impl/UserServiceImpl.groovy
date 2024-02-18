package com.example.twitter.service.impl

import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.dto.response.UserResponseDto
import com.example.twitter.exception.NotValidDataException
import com.example.twitter.exception.UserNotFoundException
import com.example.twitter.model.User
import com.example.twitter.repository.CommentRepository
import com.example.twitter.repository.LikeRepository
import com.example.twitter.repository.PostRepository
import com.example.twitter.repository.UserRepository
import com.example.twitter.mapper.Mapper
import com.example.twitter.service.UserService
import com.example.twitter.util.SecurityUtil
import com.example.twitter.util.SuccessResponse
import com.example.twitter.util.UserUtil
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class UserServiceImpl implements UserService {
    private UserRepository userRepository
    private Mapper mapper
    private SecurityUtil securityUtil
    private UserUtil userUtil
    private LikeRepository likeRepository
    private PostRepository postRepository
    private CommentRepository commentRepository

    UserServiceImpl(UserRepository userRepository, Mapper mapper, SecurityUtil securityUtil, UserUtil userUtil,
                    LikeRepository likeRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository
        this.mapper = mapper
        this.securityUtil = securityUtil
        this.userUtil = userUtil
        this.likeRepository = likeRepository
        this.postRepository = postRepository
        this.commentRepository = commentRepository
    }

    @Override
    String create(UserRequestDto userDto) {
        if (userUtil.userAlreadyExistsByName(userDto.username()) ||
                userUtil.userAlreadyExistsByNikName(userDto.nikName())) {
            throw new NotValidDataException()
        }
        def user = mapper.toModel(userDto)
        def savedUser = userRepository.save(user)
        return savedUser.id
    }

    @Override
    SuccessResponse delete(String id) {
        def user = userRepository.findById(id)
        if (user.isPresent()) {
            if (securityUtil.getCurrentUserName() == (user.get().getUsername())) {
                userRepository.delete(user.get())
                return new SuccessResponse("User was deleted successfully")
            } else {
                throw new AccessDeniedException("Account does not belong to the current user")
            }
        } else {
            throw new UserNotFoundException()
        }
    }

    @Override
    UserResponseDto update(String id, UserRequestDto userDto) {
        def currentUserOptional = userRepository.findById(id)
        if (currentUserOptional.isPresent()) {
            def currentUser = currentUserOptional.get()
            if (securityUtil.getCurrentUserName() == currentUser.getUsername()) {
                currentUser.setUsername(userDto.username())
                currentUser.setPassword(userDto.password())
                currentUser.setNikName(userDto.nikName())

                userRepository.save(currentUser)
                return mapper.toResponseDto(currentUser)
            } else {
                throw new AccessDeniedException("Account does not belong to user")
            }
        } else {
            throw new UserNotFoundException()
        }
    }

    @Override
    User findById(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow {
                    new UserNotFoundException()
                }
    }
    @Override
    User findByUserId(String id) {
        return userRepository.findById(id)
                .orElseThrow {
                    new UserNotFoundException()
                }
    }

    @Override
    SuccessResponse subscribeUser(String subscriptionUserId) {
        def currentUser = userRepository.findByUsername(securityUtil.getCurrentUserName()).get()
        def subscriptionUser = userRepository.findById(subscriptionUserId)
        if (subscriptionUser.isPresent()) {
            currentUser.getSubscription().add(subscriptionUser.get())
            userRepository.save(currentUser)
            return new SuccessResponse("Subscription user was subscribed")
        } else {
            return new SuccessResponse("User not found")
        }
    }

    @Override
    SuccessResponse unsubscribeUser(String subscriptionUserId) {
        def currentUser = userRepository.findByUsername(securityUtil.getCurrentUserName()).get()
        def subscriptionUser = userRepository.findById(subscriptionUserId)
        if (subscriptionUser.isPresent()) {
            currentUser.getSubscription().remove(subscriptionUser.get())
            userRepository.save(currentUser)
            return new SuccessResponse("Subscription user was unsubscribed")
        } else {
            return new SuccessResponse("User not found")
        }
    }

    @Override
    List<PostResponseDto> getOwnPosts(User user) {
        return user.getOwnPosts().stream()
                .map { mapper.toDto(it) }
                .toList()
    }

    @Override
    List<PostResponseDto> getLikedPosts(String username) {
        def user = userRepository.findByUsername(username).get()
        return likeRepository.findByUser(user).stream()
                .map { mapper.toDto(postRepository.findByLikesContaining(it)) }
                .toList()

    }

    @Override
    List<PostResponseDto> getCommentedPosts(String username) {
        def user = userRepository.findByUsername(username).get()
        return commentRepository.findByUser(user).stream()
                .map { mapper.toDto(postRepository.findByCommentsContaining(it)) }
                .toList()
    }

    @Override
    List<PostResponseDto> getFeed(String id) {
        def list = new ArrayList<PostResponseDto>()
        def user = userRepository.findById(id).get()
        list.addAll(commentRepository.findByUser(user)
                .collect { postRepository.findByCommentsContaining(it) }
                .findAll { it }
                .collect { mapper.toDto(it) })

        likeRepository.findByUser(user).ifPresent { like ->
            list.addAll(
                    collect { postRepository.findByLikesContaining(like) }
                            .findAll { it }
                            .collect { mapper.toDto(it) })
        }
        list.addAll(user.getOwnPosts().stream()
                .map { mapper.toDto(it) }
                .toList())
        return list
    }

    @Override
    List<PostResponseDto> getSubscriptionFeed(User user) {
        def subscriptions = user.getSubscription()
        return subscriptions.stream()
                .flatMap { getFeed(it.id).stream() }
                .toList()
    }

}
