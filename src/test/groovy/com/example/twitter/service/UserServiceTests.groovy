package com.example.twitter.service


import com.example.twitter.dto.request.UserRequestDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.dto.response.UserResponseDto
import com.example.twitter.exception.NotValidDataException
import com.example.twitter.exception.UserNotFoundException
import com.example.twitter.mapper.Mapper
import com.example.twitter.model.Comment
import com.example.twitter.model.Like
import com.example.twitter.model.Post
import com.example.twitter.model.User
import com.example.twitter.repository.CommentRepository
import com.example.twitter.repository.LikeRepository
import com.example.twitter.repository.PostRepository
import com.example.twitter.repository.UserRepository
import com.example.twitter.service.impl.UserServiceImpl
import com.example.twitter.service.util.DtoCreator
import com.example.twitter.util.SecurityUtil
import com.example.twitter.util.SuccessResponse
import com.example.twitter.util.UserUtil
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.AccessDeniedException
import spock.lang.Specification

@SpringBootTest
class UserServiceTests extends Specification {
    UserRepository userRepository = Mock()
    Mapper mapper = Mock()
    SecurityUtil securityUtil = Mock()
    UserUtil userUtil = Mock()
    LikeRepository likeRepository = Mock()
    PostRepository postRepository = Mock()
    CommentRepository commentRepository = Mock()

    UserService userService = new UserServiceImpl(userRepository, mapper, securityUtil, userUtil,
            likeRepository, postRepository, commentRepository)

    static def post = DtoCreator.createPost()

    static def user = DtoCreator.createUser()

    static def userId = "userId"

    def "create method should return id of created user"() {
        given:
        def userRequestDto = new UserRequestDto(
                username: "Vita",
                password: "123w",
                nikName: "Vita123"
        )

        when:
        def result = userService.create(userRequestDto)

        then:
        1 * userUtil.userAlreadyExistsByName("Vita") >> false
        1 * userUtil.userAlreadyExistsByNikName("Vita123") >> false
        1 * userRepository.save(_) >> { it }

        expect:
        result instanceof String
    }

    def "create method should return NotValidDataException"() {
        given:
        def userRequestDto = new UserRequestDto(
                username: "Vita",
                password: "123w",
                nikName: "Vita123"
        )

        when:
        userService.create(userRequestDto)

        then:
        1 * userUtil.userAlreadyExistsByName("Vita") >> true
        thrown(NotValidDataException.class)

    }

    def "delete method should return SuccessResponse"() {
        when:
        def result = userService.delete(userId)

        then:
        1 * userRepository.findById(userId) >> Optional.of(user)
        1 * securityUtil.getCurrentUserName() >> "Vita"
        1 * userRepository.delete(_) >> { it }

        and:
        result instanceof SuccessResponse
        result.message() == "User was deleted successfully"

    }

    def "delete method should return UserNotFoundException"() {
        when:
        userService.delete(userId)

        then:
        1 * userRepository.findById(userId) >> Optional.empty()
        thrown(UserNotFoundException.class)
    }

    def "delete method should return AccessDeniedException"() {
        when:
        userService.delete(userId)

        then:
        1 * userRepository.findById(userId) >> Optional.of(user)
        1 * securityUtil.getCurrentUserName() >> "otherId"
        thrown(AccessDeniedException.class)
    }

    def "update method should return UserResponseDto"() {
        given:
        def userRequestDto = new UserRequestDto(
                username: "VitaV",
                password: "123w",
                nikName: "Vita333")

        when:
        def result = userService.update(userId, userRequestDto)

        then:
        1 * userRepository.findById(userId) >> Optional.of(user)
        1 * securityUtil.getCurrentUserName() >> "Vita"
        1 * userRepository.save(_) >> { it }

        expect:
        result instanceof UserResponseDto
        result.nikName() == "Vita333"

    }

    def "update method should return UserNotFoundException"() {
        given:
        def userRequestDto = new UserRequestDto(
                username: "VitaV",
                password: "123w",
                nikName: "Vita333")

        when:
        userService.update(userId, userRequestDto)

        then:
        1 * userRepository.findById(userId) >> Optional.empty()
        thrown(UserNotFoundException.class)
    }

    def "update method should return AccessDeniedException"() {
        given:
        def userRequestDto = new UserRequestDto(
                username: "VitaV",
                password: "123w",
                nikName: "Vita333")

        when:
        userService.update(userId, userRequestDto)

        then:
        1 * userRepository.findById(userId) >> Optional.of(user)
        1 * securityUtil.getCurrentUserName() >> "otherId"
        def exception = thrown(AccessDeniedException.class)

        expect:
        exception.message == "Account does not belong to user"
    }

    def "subscribeUser method should return SuccessResponse"() {
        given:
        def subscriptionUser = new User(
                id: "otherId")

        when:
        def result = userService.subscribeUser(subscriptionUser.id)

        then:
        1 * securityUtil.getCurrentUserName() >> userId
        1 * userRepository.findById(userId) >> Optional.of(user)
        1 * userRepository.findById(subscriptionUser.id) >> Optional.of(subscriptionUser)
        1 * userRepository.save(_) >> { it }

        expect:
        result instanceof SuccessResponse
        result.message() == "Subscription user was subscribed"
    }

    def "subscribeUser method should return SuccessResponse user not found"() {
        given:
        def subscriptionUser = new User(
                id: "otherId")

        when:
        def result = userService.subscribeUser(subscriptionUser.id)

        then:
        1 * securityUtil.getCurrentUserName() >> userId
        1 * userRepository.findById(userId) >> Optional.of(user)
        1 * userRepository.findById(subscriptionUser.id) >> Optional.empty()

        expect:
        result instanceof SuccessResponse
        result.message() == "User not found"
    }

    def "unsubscribeUser method should return SuccessResponse "() {
        given:
        def subscriptionUser = new User(
                id: "otherId")

        when:
        def result = userService.unsubscribeUser(subscriptionUser.id)

        then:
        1 * securityUtil.getCurrentUserName() >> userId
        1 * userRepository.findById(userId) >> Optional.of(user)
        1 * userRepository.findById(subscriptionUser.id) >> Optional.of(subscriptionUser)
        1 * userRepository.save(_) >> { it }

        expect:
        result instanceof SuccessResponse
        result.message() == "Subscription user was unsubscribed"
    }

    def "unsubscribeUser method should return SuccessResponse with user not found "() {
        given:
        def subscriptionUser = new User(
                id: "otherId")

        when:
        def result = userService.unsubscribeUser(subscriptionUser.id)

        then:
        1 * securityUtil.getCurrentUserName() >> userId
        1 * userRepository.findById(userId) >> Optional.of(user)
        1 * userRepository.findById(subscriptionUser.id) >> Optional.empty()

        expect:
        result instanceof SuccessResponse
        result.message() == "User not found"
    }

    def "getOwnPosts method should return List<PostResponseDto> "() {
        given:
        def list = new HashSet<Post>()
        list.add(post)
        user.setOwnPosts(list)

        when:
        def result = userService.getOwnPosts(user)

        then:
        result instanceof List<PostResponseDto>
    }

    def "getLikedPosts method should return List<PostResponseDto> "() {
        given:
        def like = new Like()

        when:
        def result = userService.getLikedPosts(userId)

        then:
        1 * likeRepository.findByUserId(userId) >> Optional.of(like)
        1 * postRepository.findByLikesContaining(like) >> post

        result instanceof List<PostResponseDto>
    }

    def "getCommentedPosts method should return List<PostResponseDto> "() {
        given:
        def list = new ArrayList<Comment>()
        def comment = new Comment()
        list.add(comment)

        when:
        def result = userService.getCommentedPosts(userId)

        then:
        1 * commentRepository.findByUserId(userId) >> list
        1 * postRepository.findByCommentsContaining(comment) >> post

        result instanceof List<PostResponseDto>
    }

    def "getFeed method should return List<PostResponseDto> "() {
        given:
        def comment = new Comment()
        comment.setUser(user)
        def like = new Like()
        post.getComments().add(comment)


        when:
        def result = userService.getFeed(userId)

        then:
        1 * commentRepository.findByUserId(userId) >> post.getComments()
        1 * postRepository.findByCommentsContaining(comment) >> post
        1 * likeRepository.findByUserId(userId) >> Optional.of(like)
        1 * postRepository.findByLikesContaining(like) >> post
        1 * userRepository.findById(userId) >> Optional.of(user)

        result instanceof List<PostResponseDto>
    }


}
