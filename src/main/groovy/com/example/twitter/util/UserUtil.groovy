package com.example.twitter.util

import com.example.twitter.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class UserUtil {
    private UserRepository userRepository

    UserUtil(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    boolean userAlreadyExistsByName(String username) {
        userRepository.findByUsername(username).isPresent()
    }

    boolean userAlreadyExistsByNikName(String nikName) {
        userRepository.findByNikName(nikName).isPresent()
    }
}
