package com.example.twitter.repository


import com.example.twitter.model.Like
import com.example.twitter.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository extends MongoRepository<Like, String> {
    List<Like> findByPostId(String postId)

    Optional<Like> findByUser(User user)

    void deleteByPostId(String id)

}