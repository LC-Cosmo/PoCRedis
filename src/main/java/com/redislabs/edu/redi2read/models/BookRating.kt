package com.redislabs.edu.redi2read.models

import lombok.Builder
import lombok.Data
import lombok.Setter
import lombok.experimental.Accessors
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.redis.core.RedisHash
import javax.validation.constraints.NotNull

@Accessors(fluent = true)
@Data
@Builder
@RedisHash
class BookRating {

    @Id
    @Setter
    var id: String? = null

    @Reference
    var user: @NotNull User? = null

    @Reference
    var book: @NotNull Book? = null

    var rating: @NotNull Int? = null
}