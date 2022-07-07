package com.redislabs.edu.redi2read.boot

import com.redislabs.edu.redi2read.models.*
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CartRepository
import com.redislabs.edu.redi2read.services.CartService
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Consumer
import java.util.stream.IntStream

@Component
@Order(5)
@Slf4j
class CreateCarts : CommandLineRunner {
    @Autowired
    private var redisTemplate: RedisTemplate<String, String>? = null

    @Autowired
    var cartRepository: CartRepository? = null

    @Autowired
    var bookRepository: BookRepository? = null

    @Autowired
    var cartService: CartService? = null

    @Value("\${app.numberOfCarts}")
    private var numberOfCarts: Int? = null
    @Throws(Exception::class)
    override fun run(vararg args: String) {
        if (cartRepository!!.count() == 0L) {
            var random = Random()

            // loops for the number of carts to create
            IntStream.range(0, numberOfCarts!!).forEach {
                // get a random user
                var userIdB = redisTemplate!!.opsForSet() //
                    .randomMember(User::class.java.name)

                // make a cart for the user
                var cart = Cart()
//                    Cart.builder() //
//                    .userId(userId) //
//                    .build()

                // get between 1 and 7 books
                var books = getRandomBooks(bookRepository, 7)

                // add to cart
                cart.cartItems = getCartItemsForBooks(books)

                // save the cart
                cartRepository!!.save(cart)

                // randomly checkout carts
                if (random.nextBoolean()) {
                    cartService!!.checkout(cart.id)
                }
            }
            println(">>>> Created Carts...")
        }
    }

    private fun getRandomBooks(bookRepository: BookRepository?, max: Int): Set<Book> {
        var random = Random()
        var howMany = random.nextInt(max) + 1
        var books: MutableSet<Book> = HashSet()
        IntStream.range(1, howMany).forEach {
            var randomBookId = redisTemplate!!.opsForSet().randomMember(Book::class.java.name)
            books.add(bookRepository!!.findById(randomBookId).get())
        }
        return books
    }

    private fun getCartItemsForBooks(books: Set<Book>): Set<CartItem> {
        var items: MutableSet<CartItem> = HashSet()
        books.forEach(Consumer { book: Book ->
            var item = CartItem()
                item.isbn = book.id
                item.price = book.price
                item.quantity= 1L
            items.add(item)
        })
        return items
    }
}