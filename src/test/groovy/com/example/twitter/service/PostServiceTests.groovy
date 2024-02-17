package com.example.twitter.service

import com.example.twitter.dto.request.CommentRequestDto
import com.example.twitter.dto.request.PostRequestDto
import com.example.twitter.dto.response.CommentResponseDto
import com.example.twitter.dto.response.PostResponseDto
import com.example.twitter.exception.PostNotFoundException
import com.example.twitter.mapper.Mapper
import com.example.twitter.model.Comment
import com.example.twitter.model.Like
import com.example.twitter.model.Post
import com.example.twitter.model.User
import com.example.twitter.repository.CommentRepository
import com.example.twitter.repository.LikeRepository
import com.example.twitter.repository.PostRepository
import com.example.twitter.repository.UserRepository
import com.example.twitter.service.impl.PostServiceImpl
import com.example.twitter.service.util.DtoCreator
import com.example.twitter.util.SecurityUtil
import com.example.twitter.util.SuccessResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.AccessDeniedException
import spock.lang.Specification

@SpringBootTest
class PostServiceTests extends Specification {


    PostRepository postRepository = Mock()

    Mapper mapper = Mock()

    SecurityUtil securityUtil = Mock()

    UserRepository userRepository = Mock()

    CommentRepository commentRepository = Mock()

    LikeRepository likeRepository = Mock()

    PostService postService = new PostServiceImpl(postRepository, mapper, securityUtil,
            userRepository, commentRepository, likeRepository)

    static def post = DtoCreator.createPost()

    static def user = DtoCreator.createUser()

    static def postId = "123"

    static def userId = "userId"

    def "create method should return PostResponseDto"() {
        given:
        def postRequestDto = new PostRequestDto(content: "Content")

        when:
        def result = postService.create(user, postRequestDto)

        then:
        1 * postRepository.save(_) >> { post }
        1 * userRepository.save(_) >> { it }

        and:
        result instanceof PostResponseDto
    }


    def "update method should return PostResponseDto"() {
        given:

        def postRequestDto = new PostRequestDto(content: "Updated content")

        when:
        def result = postService.update(postId, postRequestDto)

        then:

        1 * postRepository.findById(postId) >> Optional.of(post)
        1 * securityUtil.getCurrentUserName() >> "Vita"
        1 * postRepository.save(_) >> { it }


        and:
        result instanceof PostResponseDto
        result.content() == "Updated content"
    }

    def "update method should throw AccessDeniedException"() {
        given:
        def postRequestDto = new PostRequestDto(content: "Updated content")

        when:
        postService.update(postId, postRequestDto)

        then:

        1 * postRepository.findById(postId) >> Optional.of(post)
        1 * securityUtil.getCurrentUserName() >> "userIncorrectId"
        def exception = thrown(AccessDeniedException)

        expect:
        exception.message == "Post does not belong to user"

    }

    def "update method should throw PostNotFoundException"() {
        given:
        def postRequestDto = new PostRequestDto(content: "Updated content")

        when:
        postService.update(postId, postRequestDto)

        then:
        1 * postRepository.findById(postId) >> Optional.empty()
        thrown(PostNotFoundException.class)
    }

    def "delete method should return SuccessResponse with deleting the post"() {
        given:
        def userId = "Vita"

        when:
        def result = postService.delete(postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)
        2 * securityUtil.getCurrentUserName() >> userId
        1 * userRepository.findByUsername(userId) >> Optional.of(user)
        1 * likeRepository.deleteByPostId(_) >> { it }
        1 * commentRepository.deleteByPostId(_) >> { it }
        1 * postRepository.deleteById(_) >> { it }

        and:
        result instanceof SuccessResponse
        result.message() == "Post was deleted successfully"
    }

    def "delete method should return SuccessResponse with post not found"() {
        when:
        def result = postService.delete(postId)

        then:
        1 * postRepository.findById(postId) >> Optional.empty()

        expect:
        result instanceof SuccessResponse
        result.message() == "Post does not exist"
    }

    def "delete method should return AccessDeniedException"() {
        given:
        def unrelatedUser = DtoCreator.createUser()
        def incorrectId = "userIncorrectId"
        unrelatedUser.setId(incorrectId)

        when:
        postService.delete(postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)
        2 * securityUtil.getCurrentUserName() >> incorrectId
        1 * userRepository.findByUsername(incorrectId) >> Optional.of(unrelatedUser)
        def exception = thrown(AccessDeniedException)

        expect:
        exception.message == "Post does not belong to user"

    }

    def "commentPost method should return SuccessResponse"() {
        given:
        def commentDto = new CommentRequestDto("Comment")

        when:
        def result = postService.commentPost(user, commentDto, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)
        1 * postRepository.save(_) >> { it }

        and:
        result instanceof SuccessResponse
        result.message() == "Comment was added successfully"

    }

    def "commentPost method should return SuccessResponse with PostIsNotFound"() {
        given:
        def commentDto = new CommentRequestDto("Comment")

        when:
        def result = postService.commentPost(user, commentDto, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.empty()

        and:
        result instanceof SuccessResponse
        result.message() == "Post is not found"

    }

    def "getAllComments method should return SuccessResponse"() {
        given:
        def list = new ArrayList<Comment>()
        def comment = DtoCreator.createComment()
        def comment2 = DtoCreator.createComment()
        comment.setUser(user)
        comment2.setUser(user)
        list.add(comment)
        list.add(comment2)
        post.setComments(list)

        when:
        def result = postService.getAllComments(postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)

        expect:
        result instanceof List<CommentResponseDto>
    }

    def "getAllComments method should return SuccessResponse with PostNotFoundException"() {
        when:
        postService.getAllComments(postId)

        then:
        1 * postRepository.findById(postId) >> Optional.empty()
        thrown(PostNotFoundException)
    }

    def "leaveLike method should return SuccessResponse with like"() {
        given:
        def like = (new Like(
                user: new User(id: "otherId"),
                post: post))

        def likes = new HashSet<Like>()
        likes.add(like)
        post.setLikes(likes)

        when:
        def result = postService.leaveLike(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)
        1 * likeRepository.save(_) >> { like }
        1 * postRepository.save(_) >> { it }

        and:
        result instanceof SuccessResponse
        result.message() == "Like was added successfully"
    }

    def "leaveLike method should return with already like SuccessResponse"() {
        given:
        def like = (new Like(
                user: user,
                post: post))

        def likes = new HashSet<Like>()
        likes.add(like)
        post.setLikes(likes)

        when:
        def result = postService.leaveLike(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)

        and:
        result instanceof SuccessResponse
        result.message() == "You have already liked this post "
    }

    def "leaveLike method should return post is not found SuccessResponse"() {
        when:
        def result = postService.leaveLike(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.empty()

        and:
        result instanceof SuccessResponse
        result.message() == "Post is not found"
    }

    def "deleteLike method should SuccessResponse with deleting like"() {
        given:
        def like = (new Like(
                user: user,
                post: post))
        when:
        def result = postService.removeLike(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)
        1 * likeRepository.findByUserId(userId) >> Optional.of(like)
        1 * likeRepository.delete(_) >> { it }
        1 * postRepository.save(_) >> { it }


        and:
        result instanceof SuccessResponse
        result.message() == "Like was deleted successfully"
    }

    def "deleteLike method should SuccessResponse with non-existing like"() {
        when:
        def result = postService.removeLike(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)
        1 * likeRepository.findByUserId(userId) >> Optional.empty()

        and:
        result instanceof SuccessResponse
        result.message() == "User did not leave like here"
    }

    def "deleteLike method should SuccessResponse with non-existing post"() {
        when:
        def result = postService.removeLike(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.empty()

        and:
        result instanceof SuccessResponse
        result.message() == "Post is not found"
    }

    def "addToFavorite method should SuccessResponse with addingToFavorite"() {
        when:
        def result = postService.addToFavorite(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)
        1 * userRepository.save(_) >> { it }

        and:
        result instanceof SuccessResponse
        result.message() == "Post was added to favorite successfully"
    }

    def "addToFavorite method should SuccessResponse with already in favorite"() {
        user.getFavoritePosts().add(post)

        when:
        def result = postService.addToFavorite(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)

        and:
        result instanceof SuccessResponse
        result.message() == "This post is already in favorites"
    }

    def "addToFavorite method should SuccessResponse with already in favorite"() {
        when:
        def result = postService.addToFavorite(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.empty()

        and:
        result instanceof SuccessResponse
        result.message() == "Post is not found"
    }

    def "removeFavorite method should SuccessResponse with deleting from favorite"() {
        when:
        def result = postService.removeFromFavorite(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.of(post)
        1 * userRepository.save(_) >> { it }

        and:
        result instanceof SuccessResponse
        result.message() == "Post was deleted from favorite successfully"
    }

    def "removeFavorite method should SuccessResponse with deleting from favorite"() {
        when:
        def result = postService.removeFromFavorite(user, postId)

        then:
        1 * postRepository.findById(postId) >> Optional.empty()

        and:
        result instanceof SuccessResponse
        result.message() == "Post is not found"
    }

}
