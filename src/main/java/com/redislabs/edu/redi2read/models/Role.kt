package com.redislabs.edu.redi2read.models

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import lombok.Builder
import lombok.Data
import lombok.Getter
import lombok.Setter
import lombok.experimental.Accessors
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@Accessors(fluent = true)
@Data
@Builder
@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@RedisHash
class Role {

    @Id
    @Setter
    var id: String? = null

    @Indexed
    var name: String? = null
}