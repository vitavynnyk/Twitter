package com.example.twitter.model

import groovy.transform.builder.Builder
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference

import javax.validation.constraints.NotNull
import java.time.Instant

@Document
@Builder
@AllArgsConstructor
class Like {
    @Id
    String id

    @DocumentReference
    User user

    @DocumentReference
    Post post

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Like like = (Like) o

        if (id != like.id) return false
        if (post != like.post) return false
        if (user != like.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (post != null ? post.hashCode() : 0)
        return result
    }
}
