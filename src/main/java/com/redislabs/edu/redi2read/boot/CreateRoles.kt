package com.redislabs.edu.redi2read.boot

import com.redislabs.edu.redi2read.models.Role
import com.redislabs.edu.redi2read.repositories.RoleRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(1)
@Slf4j
class CreateRoles : CommandLineRunner {
    @Autowired
    private lateinit var roleRepository: RoleRepository
    @Throws(Exception::class)
    override fun run(vararg args: String) {
        if (roleRepository!!.count() == 0L) {
            var adminRole = Role()
                adminRole.name = "admin"
            var customerRole = Role()
                customerRole.name = "customer"
            roleRepository!!.save(adminRole)
            roleRepository!!.save(customerRole)
            println(">>>> Created admin and customer roles...")
        }
    }
}