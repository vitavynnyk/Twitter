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
@NoArgsConstructor
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


    @Override
    String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", content='" + content + '\'' +
                ", creatingDate=" + creatingDate +
                '}'
    }
}
