package com.redislabs.edu.redi2read.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@RedisHash
public class Role {
    @Id
    private String id;

    @Indexed
    private String name;
}