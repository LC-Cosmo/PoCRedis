package com.redislabs.edu.redi2read.models

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.Setter
import lombok.experimental.Accessors
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.redis.core.RedisHash


@Accessors(fluent = true)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@RedisHash
class Book {

    @Id
    @Setter
    @EqualsAndHashCode.Include
    var id: String  = ""
    var title: String? = ""
    var subtitle: String? = ""
    var description: String? = ""
    var language: String? = ""
    var pageCount: Long? = 0
    var thumbnail: String? = ""
    var price: Double? = 0.0
    var currency: String? = ""
    var infoLink: String? = ""
    lateinit var authors: Set<String>

    @Reference
    private var categories: MutableSet<Category> = HashSet()
    fun addCategory(category: Category) {
        categories.add(category)
    }
}