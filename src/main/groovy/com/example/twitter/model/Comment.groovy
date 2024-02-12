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
class Comment {

    @Id
    String id

    @DocumentReference
    User user

    @DocumentReference
    Post post

    @NotNull
    String content

    @NotNull
    Instant creatingDate
}
