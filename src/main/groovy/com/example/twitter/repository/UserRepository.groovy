package com.example.twitter.repository


import com.example.twitter.model.User
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String email)

    Optional<User> findByNikName(String nikName)

    Set<User> findAllBySubscription(User user)

}