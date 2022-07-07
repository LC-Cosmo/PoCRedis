package com.redislabs.edu.redi2read.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/redis")
class HelloRedisController {
    @Autowired
    private var template: RedisTemplate<String, String>? = null
    @PostMapping("/strings")
    @ResponseStatus(HttpStatus.CREATED)
    fun setString(@RequestBody kvp: Map.Entry<String, String>): Map.Entry<String, String> {
        template!!.opsForValue()[STRING_KEY_PREFIX + kvp.key] = kvp.value
        return kvp
    }

    @GetMapping("/strings/{key}")
    fun getString(@PathVariable("key") key: String): Map.Entry<String, String> {
        var value = template!!.opsForValue()[STRING_KEY_PREFIX + key]
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "key not found")
        return AbstractMap.SimpleEntry(key, value)
    }

    companion object {
        private const val STRING_KEY_PREFIX = "redi2read:strings:"
    }
}