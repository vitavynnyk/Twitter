package com.example.twitter.controller

import com.example.twitter.controller.config.Configuration
import com.example.twitter.repository.UserRepository
import com.example.twitter.service.util.DtoCreator
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Import(Configuration)
@ActiveProfiles("test")
class UserControllerTest {
    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    UserRepository userRepository

    static final String USER_URL = "/api/users"


    def cleanup() {
        userRepository.deleteAll()
    }

    @Test
    void "should return HTTP status 201 when creating a user"() {
        given:
        def userRequestDto = DtoCreator.createUserRequestDto()
        def json = objectMapper.writeValueAsString(userRequestDto)

        when:
        def result = mockMvc.perform(post(USER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        then:
        result.andExpect(status().isCreated())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when deleting a user"() {
        given:
        def user = userRepository.save(DtoCreator.createUserInDataBase())

        when:
        def result = mockMvc.perform(delete(USER_URL + "/" + user.id)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User was deleted successfully"))

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Viktor")
    void "should return HTTP status 200 when deleting a user with other account"() {
        given:
        def user = userRepository.save(DtoCreator.createUser())

        when:
        def result = mockMvc.perform(delete(USER_URL + "/" + user.id)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isForbidden())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 404 when deleting a user "() {
        when:
        def result = mockMvc.perform(delete(USER_URL + "/123")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isNotFound())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when updating a user"() {
        given:
        def user = userRepository.save(DtoCreator.createUserInDataBase())
        def userDto = DtoCreator.createUserRequestDto()
        def json = objectMapper.writeValueAsString(userDto)

        when:
        def result = mockMvc.perform(patch(USER_URL + "/" + user.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(user.id))

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 404 when updating a user"() {
        given:
        def userDto = DtoCreator.createUserRequestDto()
        def json = objectMapper.writeValueAsString(userDto)

        when:
        def result = mockMvc.perform(patch(USER_URL + "/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        then:
        result.andExpect(status().isNotFound())
        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Viktor")
    void "should return HTTP status 403 when updating a user"() {
        given:
        def user = userRepository.save(DtoCreator.createUserInDataBase())
        def userDto = DtoCreator.createUserRequestDto()
        def json = objectMapper.writeValueAsString(userDto)

        when:
        def result = mockMvc.perform(patch(USER_URL + "/" + user.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        then:
        result.andExpect(status().isForbidden())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when getting own posts"() {
        given:
        userRepository.save(DtoCreator.createUserInDataBase())

        when:
        def result = mockMvc.perform(get(USER_URL + "/my-posts")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when getting own likes"() {
        given:
        userRepository.save(DtoCreator.createUserInDataBase())

        when:
        def result = mockMvc.perform(get(USER_URL + "/my-likes")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())

        cleanup()
    }

}
