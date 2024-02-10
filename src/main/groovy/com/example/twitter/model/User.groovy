package com.example.twitter.model

import com.example.twitter.model.enums.AuthorityType
import groovy.transform.builder.Builder
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

import javax.validation.constraints.NotNull

@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
class User {
    @Id
    String id


//    @Indexed(unique = true)
    @NotNull
    String username

    @NotNull
    String password

    @NotNull
    String nikName

    Boolean isActive

    Set<AuthorityType> roles

    @DocumentReference
    Set<Post> ownPosts

    Set<User> subscription




    @Override
    String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}'
    }


}
