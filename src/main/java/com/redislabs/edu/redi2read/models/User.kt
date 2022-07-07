package com.redislabs.edu.redi2read.models

import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.annotation.Transient
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@JsonIgnoreProperties(value = ["password", "passwordConfirm"], allowSetters = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Data
@RedisHash
class User {
    @Id
    @ToString.Include
    var id: String? = null

    @ToString.Include
    var name: @NotNull @Size(min = 2, max = 48) String? = null

    @EqualsAndHashCode.Include
    @ToString.Include
    @Indexed
    var email: @NotNull @Email String? = null
    var password: @NotNull String? = null

    @Transient
    private var passwordConfirm: String? = null

    @Reference
    @JsonIdentityReference(alwaysAsId = true)
    private var roles: MutableSet<Role> = HashSet()
    fun addRole(role: Role) {
        roles.add(role)
    }

    @Reference
    @JsonIdentityReference(alwaysAsId = true)
    private var books: MutableSet<Book> = HashSet()
    fun addBook(book: Book) {
        books.add(book)
    }
}