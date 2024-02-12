package com.example.twitter.repository

import com.example.twitter.model.Comment
import com.example.twitter.model.Post
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findAllByPostId ( String id)

}