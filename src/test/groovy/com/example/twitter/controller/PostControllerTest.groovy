package com.example.twitter.controller

import com.example.twitter.controller.config.Configuration
import com.example.twitter.mapper.Mapper
import com.example.twitter.repository.CommentRepository
import com.example.twitter.repository.LikeRepository
import com.example.twitter.repository.PostRepository
import com.example.twitter.repository.UserRepository
import com.example.twitter.service.util.DtoCreator
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Import(Configuration)
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    PasswordEncoder passwordEncoder

    @Autowired
    Mapper mapper

    @Autowired
    UserRepository userRepository

    @Autowired
    PostRepository postRepository

    @Autowired
    CommentRepository commentRepository

    @Autowired
    LikeRepository likeRepository


    static final String POST_URL = "/api/posts"

    def setup() {
        userRepository.save(DtoCreator.createUser())
    }

    def cleanup() {
        userRepository.deleteAll()
        postRepository.deleteAll()
        commentRepository.deleteAll()
    }


    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 201 when creating a post"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostRequestDto()
        def json = objectMapper.writeValueAsString(postRequestDto)

        when:
        def result = mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        then:
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("content").value("Content"))

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Viktor")
    void "should return HTTP status 404 when creating a post"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostRequestDto()
        def json = objectMapper.writeValueAsString(postRequestDto)

        when:
        def result = mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        then:
        result.andExpect(status().isNotFound())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "Vita")
    void "should return HTTP status 403 when creating a post"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostRequestDto()
        def json = objectMapper.writeValueAsString(postRequestDto)

        when:
        def result = mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        then:
        result.andExpect(status().isForbidden())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when updating a post"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostUpdatingRequestDto()
        def newJson = objectMapper.writeValueAsString(postRequestDto)
        postRepository.save(DtoCreator.createPost())


        when:
        def result = mockMvc.perform(patch(POST_URL + "/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newJson))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("content").value("New content"))

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 404 when updating a post"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostUpdatingRequestDto()
        def newJson = objectMapper.writeValueAsString(postRequestDto)


        when:
        def result = mockMvc.perform(patch(POST_URL + "/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newJson))

        then:
        result.andExpect(status().isNotFound())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "Vita")
    void "should return HTTP status 403 when updating a post"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostUpdatingRequestDto()
        def newJson = objectMapper.writeValueAsString(postRequestDto)
        postRepository.save(DtoCreator.createPost())


        when:
        def result = mockMvc.perform(patch(POST_URL + "/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newJson))

        then:
        result.andExpect(status().isForbidden())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Viktor")
    void "should return HTTP status 403 when updating a post with unrelated user"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostUpdatingRequestDto()
        def newJson = objectMapper.writeValueAsString(postRequestDto)
        postRepository.save(DtoCreator.createPost())


        when:
        def result = mockMvc.perform(patch(POST_URL + "/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newJson))

        then:
        result.andExpect(status().isForbidden())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when deleting a post"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostRequestDto()
        def newJson = objectMapper.writeValueAsString(postRequestDto)
        postRepository.save(DtoCreator.createPost())


        when:
        def result = mockMvc.perform(delete(POST_URL + "/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newJson))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Post was deleted successfully"))

        cleanup()
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "Vita")
    void "should return HTTP status 403 when deleting a post"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostRequestDto()
        def newJson = objectMapper.writeValueAsString(postRequestDto)
        postRepository.save(DtoCreator.createPost())


        when:
        def result = mockMvc.perform(delete(POST_URL + "/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newJson))

        then:
        result.andExpect(status().isForbidden())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Viktor")
    void "should return HTTP status 403 when deleting a post with unrelated user"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostRequestDto()
        def newJson = objectMapper.writeValueAsString(postRequestDto)
        postRepository.save(DtoCreator.createPost())


        when:
        def result = mockMvc.perform(delete(POST_URL + "/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newJson))
        println result.andReturn().response.contentAsString

        then:
        result.andExpect(status().isForbidden())

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 404 when deleting a post"() {
        setup()

        given:
        def postRequestDto = DtoCreator.createPostRequestDto()
        def newJson = objectMapper.writeValueAsString(postRequestDto)

        when:
        def result = mockMvc.perform(delete(POST_URL + "/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newJson))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Post does not exist"))

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when commenting a post"() {
        setup()

        given:
        postRepository.save(DtoCreator.createPost())
        def commentDto = DtoCreator.createPostRequestDto()
        def newJson = objectMapper.writeValueAsString(commentDto)

        when:
        def result = mockMvc.perform(patch(POST_URL + "/123/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newJson))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Comment was added successfully"))

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 404 when commenting a post"() {
        setup()

        given:

        def commentDto = DtoCreator.createPostRequestDto()
        def newJson = objectMapper.writeValueAsString(commentDto)

        when:
        def result = mockMvc.perform(patch(POST_URL + "/123/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newJson))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Post is not found"))

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 and list of comments"() {
        setup()

        given:
        def post = postRepository.save(DtoCreator.createPost())
        def comment = commentRepository.save(DtoCreator.createComment())
        def comment2 = commentRepository.save(DtoCreator.createComment())
        post.comments.add(comment)
        post.comments.add(comment2)
        postRepository.save(post)


        when:
        def result = mockMvc.perform(get(POST_URL + "/123/comments")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when leaving a like"() {
        setup()

        given:
        postRepository.save(DtoCreator.createPost())

        when:
        def result = mockMvc.perform(patch(POST_URL + "/123/like")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Like was added successfully"))


        cleanup()
        likeRepository.deleteAll()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 403 when leaving a like with already liked user"() {

        given:
        def user = userRepository.save(DtoCreator.createUserInDataBase())
        def post = postRepository.save(DtoCreator.createPost())
        def like = likeRepository.save(DtoCreator.createLike())
        like.setUser(user)
        likeRepository.save(like)
        post.likes.add(like)
        postRepository.save(post)


        when:
        def result = mockMvc.perform(patch(POST_URL + "/123/like")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("You have already liked this post "))

        cleanup()
        likeRepository.deleteAll()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when removing a like"() {

        given:
        def user = userRepository.save(DtoCreator.createUserInDataBase())
        def post = postRepository.save(DtoCreator.createPost())
        def like = likeRepository.save(DtoCreator.createLike())
        like.setUser(user)
        likeRepository.save(like)
        post.likes.add(like)
        postRepository.save(post)

        when:
        def result = mockMvc.perform(patch(POST_URL + "/123/unlike")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Like was deleted successfully"))


        cleanup()
        likeRepository.deleteAll()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when removing a like with non-lived like"() {
        setup()

        given:
        postRepository.save(DtoCreator.createPost())

        when:
        def result = mockMvc.perform(patch(POST_URL + "/123/unlike")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User did not leave like here"))


        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when removing a like with non-existed post"() {
        setup()

        when:
        def result = mockMvc.perform(patch(POST_URL + "/123/unlike")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Post is not found"))

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when adding to favorite"() {
        setup()
        given:
        postRepository.save(DtoCreator.createPost())

        when:
        def result = mockMvc.perform(patch(POST_URL + "/123/favorite")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Post was added to favorite successfully"))

        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when adding to favorite with already added post"() {
        cleanup()
        given:
        def user = DtoCreator.createUserInDataBase()
        def post = postRepository.save(DtoCreator.createPost())
        user.getFavoritePosts().add(post)
        userRepository.save(user)
        println post.id

        when:
        def result = mockMvc.perform(patch(POST_URL + "/" + post.id + "/favorite")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("This post is already in favorites"))
        cleanup()
    }

    @Test
    @WithMockUser(roles = "USER", username = "Vita")
    void "should return HTTP status 200 when removing from favorite"() {
        given:
        def user = DtoCreator.createUserInDataBase()
        def post = postRepository.save(DtoCreator.createPost())
        user.favoritePosts.add(post)
        userRepository.save(user)

        when:
        def result = mockMvc.perform(patch(POST_URL + "/123/delete-from-favorite")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Post was deleted from favorite successfully"))
        cleanup()
    }


}