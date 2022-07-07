package com.redislabs.edu.redi2read

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
open class Redi2readApplication {
    @Bean
    open fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<*, *> {
        var template: RedisTemplate<*, *> = RedisTemplate<Any, Any>()
        template.connectionFactory = connectionFactory
        return template
    }

    @Bean
    open fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    companion object {
        @JvmStatic
        open fun main(args: Array<String>) {
            SpringApplication.run(Redi2readApplication::class.java, *args)
        }
    }
}