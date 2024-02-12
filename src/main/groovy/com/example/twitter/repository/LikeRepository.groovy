package com.example.twitter.repository

import com.example.twitter.model.Comment
import com.example.twitter.model.Like
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository extends MongoRepository<Like, String> {
    Optional<Like> findByPostId(String postId)

}