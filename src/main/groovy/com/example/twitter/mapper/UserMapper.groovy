package com.example.twitter.mapper

import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.UserResponseDto
import com.example.twitter.model.Post
import com.example.twitter.model.User
import com.example.twitter.model.enums.AuthorityType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserMapper {

    PasswordEncoder passwordEncoder

    UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder
    }

    UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                id: user.getId(),
                nikName: user.getNikName(),
                roles: user.getRoles()
        )

    }

    User toModel(UserRequestDto userDto) {
        return new User(
                username: userDto.username(),
                password: passwordEncoder.encode(userDto.password()),
                isActive: true,
                roles: Set.of(AuthorityType.USER),
                nikName: userDto.nikName(),
                ownPosts: new HashSet<Post>())

    }
}
