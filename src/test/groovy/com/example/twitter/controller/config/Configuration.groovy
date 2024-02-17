package com.example.twitter.controller.config

import com.example.twitter.mapper.Mapper
import com.example.twitter.service.PostService
import com.example.twitter.service.UserService
import com.example.twitter.service.impl.PostServiceImpl
import com.example.twitter.service.impl.UserServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@TestConfiguration
class Configuration {
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper()

    }

}