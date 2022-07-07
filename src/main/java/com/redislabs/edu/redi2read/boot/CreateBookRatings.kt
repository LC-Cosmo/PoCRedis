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
    private var numberOfRatings: Int? = null

    @Value("\${app.ratingStars}")
    private var ratingStars: Int? = null

    @Autowired
    private var redisTemplate: RedisTemplate<String, String>? = null

    @Autowired
    private var bookRatingRepo: BookRatingRepository? = null
    @Throws(Exception::class)
    override fun run(vararg args: String) {
        if (bookRatingRepo!!.count() == 0L) {
            var random = Random()
            IntStream.range(0, numberOfRatings!!).forEach {
                var bookId = redisTemplate!!.opsForSet().randomMember(Book::class.java.name)
                val userId = redisTemplate!!.opsForSet().randomMember(User::class.java.name)
                var stars = random.nextInt(ratingStars!!) + 1
                var user = User()
                user.id = userId
                var book = Book()
                book.id = bookId
                var rating = BookRating()
                    rating.user = user
                    rating.book = book
                    rating.rating = stars
                bookRatingRepo!!.save(rating)
            }
            println(">>>> BookRating created...")
        }
    }
}