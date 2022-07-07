package com.redislabs.edu.redi2read.models

import lombok.Builder
import lombok.Data
import lombok.Setter
import lombok.experimental.Accessors
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@Accessors(fluent = true)
@Data
@Builder
@RedisHash
class Category {
    @Id
    @Setter
    var id: String? = null
    var name: String? = null
}