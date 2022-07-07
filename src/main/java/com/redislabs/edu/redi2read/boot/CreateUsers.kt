package com.redislabs.edu.redi2read.boot

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.redislabs.edu.redi2read.models.User
import com.redislabs.edu.redi2read.repositories.RoleRepository
import com.redislabs.edu.redi2read.repositories.UserRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.io.IOException

@Component
@Order(2)
@Slf4j
class CreateUsers : CommandLineRunner {
    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: BCryptPasswordEncoder
    @Throws(Exception::class)
    override fun run(vararg args: String) {
        if (userRepository.count() == 0L) {
            // load the roles
            val admin = roleRepository.findFirstByName("admin")
            val customer = roleRepository.findFirstByName("customer")
            try {
                // create a Jackson object mapper
                val mapper = ObjectMapper()
                // create a type definition to convert the array of JSON into a List of Users
                val typeReference: TypeReference<List<User?>?> = object : TypeReference<List<User?>?>() {}
                // make the JSON data available as an input stream
                val inputStream = javaClass.getResourceAsStream("/data/users/users.json")
                // convert the JSON to objects
                val users = mapper.readValue(inputStream, typeReference)
                users?.stream()?.forEach { user: User? ->
                    user?.password = passwordEncoder.encode(user!!.password)
                    if (customer != null) {
                        user.addRole(customer)
                    }
                    userRepository.save(user)
                }
                if (users != null) {
                    println(">>>> " + users.size + " Users Saved!")
                }
            } catch (e: IOException) {
                println(">>>> Unable to import users: " + e.message)
            }
            val adminUser = User()
            adminUser.name = "Adminus Admistradore"
            adminUser.email = "admin@example.com"
            adminUser.password = passwordEncoder.encode("Reindeer Flotilla") //
            if (admin != null) {
                adminUser.addRole(admin)
            }
            userRepository.save(adminUser)
            println(">>>> Loaded User Data and Created users...")
        }
    }
}