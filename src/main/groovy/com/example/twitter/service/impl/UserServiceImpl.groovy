package com.example.twitter.service.impl

import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.exception.NotValidDataException
import com.example.twitter.exception.UserNotFoundException
import com.example.twitter.model.User
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

    UserServiceImpl(UserRepository userRepository, Mapper userMapper, SecurityUtil securityUtil, UserUtil userUtil) {
        this.userRepository = userRepository
        this.mapper = userMapper
        this.securityUtil = securityUtil
        this.userUtil = userUtil
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
        if (securityUtil.getCurrentUserId() == id) {
            var user = userRepository.findById(id)
            user.ifPresentOrElse({ u -> userRepository.delete(u) },
                    { -> throw new UserNotFoundException() })
        } else {
            throw new AccessDeniedException("Account does not belong to user")
        }
        return new SuccessResponse("User was deleted successfully")
    }

    @Override
    void update(String id, UserRequestDto userDto) {
        if (securityUtil.getCurrentUserId() == id) {
            def currentUser = userRepository.findById(id)
            currentUser.ifPresentOrElse({ user ->
                user.setUsername(userDto.username())
                user.setPassword(userDto.password())

                userRepository.save(user)
            },
                    { -> throw new UserNotFoundException() })
        } else {
            throw new AccessDeniedException("Account does not belong to user")
        }
    }

    @Override
    User findById(String id) {
        return userRepository.findById(id).get() ?: { throw new UserNotFoundException() }()
    }

    @Override
    SuccessResponse subscribeUser(String subscriptionUserId) {
        def currentUser = userRepository.findById(securityUtil.getCurrentUserId()).get()
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
        def currentUser = userRepository.findById(securityUtil.getCurrentUserId()).get()
        def subscriptionUser = userRepository.findById(subscriptionUserId)
        if (subscriptionUser.isPresent()) {
            currentUser.getSubscription().remove(subscriptionUser.get())
            userRepository.save(currentUser)
            return new SuccessResponse("Subscription user was unsubscribed")
        } else {
            return new SuccessResponse("User not found")
        }
    }
}
