package com.example.twitter.config

import com.example.twitter.model.User
import com.example.twitter.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User.UserBuilder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

import static org.springframework.security.core.userdetails.User.withUsername

@Component
class CustomUserDetailsService implements UserDetailsService {

    UserRepository userRepository

    @Autowired
    CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository

    }


    @Override
    UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(userName)
        UserBuilder userDetailsBuilder = withUsername(user.get().getUsername())
        String[] roles = user.get().getRoles().stream()
                .map(role -> role.toString())
                .toArray(String[]::new)

        return userDetailsBuilder
                .password(user.get().getPassword())
//                .username(user.get().id)
                .disabled(!user.get().isActive)
                .roles(roles)
                .build()
    }
}
