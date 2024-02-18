package com.example.twitter.model

import com.example.twitter.model.enums.AuthorityType
import groovy.transform.builder.Builder
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import org.springframework.data.repository.cdi.Eager

import javax.validation.constraints.NotNull

@Document
@Builder
@AllArgsConstructor
class User {
    @Id
    String id

    @NotNull
    String username

    @NotNull
    String password

    @NotNull
    String nikName

    @NotNull
    Boolean isActive

    @NotNull
    Set<AuthorityType> roles

    @DocumentReference
    Set<Post> ownPosts

    @DocumentReference
    Set<Post> favoritePosts

    @DocumentReference
    Set<User> subscription

    @Override
    String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nikName='" + nikName + '\'' +
                ", isActive=" + isActive +
                ", roles=" + roles +
                ", ownPosts=" + ownPosts +
                ", subscription=" + subscription +
                '}'
    }


}
