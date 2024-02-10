package com.example.twitter.service.impl

import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.exception.NotValidDataException
import com.example.twitter.exception.UserNotFoundException
import com.example.twitter.model.User
import com.example.twitter.repository.UserRepository
import com.example.twitter.mapper.UserMapper
import com.example.twitter.service.UserService
import com.example.twitter.util.SecurityUtil
import com.example.twitter.util.SuccessResponse
import com.example.twitter.util.UserUtil
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class UserServiceImpl implements UserService {
    private UserRepository userRepository
    private UserMapper userMapper
    private SecurityUtil securityUtil
    private UserUtil userUtil

    UserServiceImpl(UserRepository userRepository, UserMapper userMapper, SecurityUtil securityUtil, UserUtil userUtil) {
        this.userRepository = userRepository
        this.userMapper = userMapper
        this.securityUtil = securityUtil
        this.userUtil = userUtil
    }

    @Override
    String create(UserRequestDto userDto) {
        if (userUtil.userAlreadyExistsByName(userDto.username()) ||
                userUtil.userAlreadyExistsByNikName(userDto.nikName())) {
            throw new NotValidDataException()
        }
        var user = userMapper.toModel(userDto)
        var savedUser = userRepository.save(user)
        return savedUser.id
    }

    @Override
    SuccessResponse delete(String id) {
        var userId = securityUtil.getCurrentUserId()
        if (userId == id) {
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
        var userId = securityUtil.getCurrentUserId()
        var user = userRepository.findById(id)
        user.ifPresentOrElse({ u ->
            u.setUsername(userDto.username())
            u.setPassword(userDto.password())

            userRepository.save(u)
        },
                { -> throw new UserNotFoundException() })

    }

    @Override
    User findById(String id) {
        return userRepository.findById(id).get() ?: { throw new UserNotFoundException() }()
    }


//    @Override
//    @Transactional
//    UserResponseDto findByEmail(String email) {
//        def user = userRepository.findByUsername(email) ?: { throw new UserNotFoundException() }()
//        return userMapper.toResponseDto(user.get());
//    }


}
