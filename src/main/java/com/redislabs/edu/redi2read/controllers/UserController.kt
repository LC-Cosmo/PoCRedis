package com.redislabs.edu.redi2read.controllers

import com.redislabs.edu.redi2read.models.User
import com.redislabs.edu.redi2read.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.function.Function

@RestController
@RequestMapping("/api/users")
class UserController {
    @Autowired
    private var userRepository: UserRepository? = null
    @GetMapping
    fun all(@RequestParam(defaultValue = "") email: String): Iterable<User?>? {
        return if (email.isEmpty()) {
            userRepository!!.findAll()
        } else {
            var user = Optional.ofNullable(
                userRepository!!.findFirstByEmail(email)
            )
            user.map(Function<User, List<User>> { e1: User? -> listOf(e1) as List<User>? }).orElse(emptyList())
        }
    }
}