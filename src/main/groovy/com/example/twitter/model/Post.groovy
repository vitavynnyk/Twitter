package com.example.twitter.model

import groovy.transform.builder.Builder
import lombok.AllArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

import javax.validation.constraints.NotNull
import java.time.Instant

@Document
@Builder
@AllArgsConstructor
class Post {
    @Id
    String id

    @NotNull
    @DocumentReference
    User user

    @NotNull
    String content

    @NotNull
    Instant creatingDate

    @DocumentReference
    List<Comment> comments

    @DocumentReference
    Set<Like> likes

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Post post = (Post) o

        if (content != post.content) return false
        if (creatingDate != post.creatingDate) return false
        if (id != post.id) return false
        if (user != post.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (content != null ? content.hashCode() : 0)
        result = 31 * result + (creatingDate != null ? creatingDate.hashCode() : 0)
        return result
    }
}
