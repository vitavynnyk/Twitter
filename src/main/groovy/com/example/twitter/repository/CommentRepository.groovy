package com.example.twitter.repository

import com.example.twitter.model.Comment
import com.example.twitter.model.User
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByUser(User user)

    void deleteByPostId(String id)
}