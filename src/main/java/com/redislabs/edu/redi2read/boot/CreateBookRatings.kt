package com.redislabs.edu.redi2read.boot

import com.redislabs.edu.redi2read.models.*
import com.redislabs.edu.redi2read.repositories.BookRatingRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.IntStream

@Component
@Order(4)
@Slf4j
class CreateBookRatings : CommandLineRunner {
    @Value("\${app.numberOfRatings}")
    var numberOfRatings: Int? = null

    @Value("\${app.ratingStars}")
    private var ratingStars: Int? = null

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    private lateinit var bookRatingRepo: BookRatingRepository
    @Throws(Exception::class)
    override fun run(vararg args: String) {
        if (bookRatingRepo.count() == 0L) {
            val random = Random()
            IntStream.range(0, numberOfRatings!!).forEach {
                val bookId = redisTemplate.opsForSet().randomMember(Book::class.java.name)
                val userId = redisTemplate.opsForSet().randomMember(User::class.java.name)
                val stars = random.nextInt(ratingStars!!) + 1
                val user = User()
                user.id = userId
                val book = Book()
                book.id = bookId
                val rating = BookRating()
                    rating.user = user
                    rating.book = book
                    rating.rating = stars
                bookRatingRepo.save(rating)
            }
            println(">>>> BookRating created...")
        }
    }
}