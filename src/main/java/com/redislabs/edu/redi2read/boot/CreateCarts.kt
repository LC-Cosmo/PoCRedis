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
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    lateinit var cartRepository: CartRepository

    @Autowired
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var cartService: CartService

    @Value("\${app.numberOfCarts}")
    private var numberOfCarts: Int? = 0
    @Throws(Exception::class)
    override fun run(vararg args: String) {
        if (cartRepository.count() == 0L) {
            val random = Random()

            // loops for the number of carts to create
            IntStream.range(0, numberOfCarts!!).forEach {
                // get a random user
                val userId = redisTemplate.opsForSet() //
                    .randomMember(User::class.java.name)

                // make a cart for the user
                val cart = Cart()
                print(userId)
                cart.userId = userId
                // get between 1 and 7 books
                val books = getRandomBooks(bookRepository, 7)

                // add to cart
                cart.cartItems = getCartItemsForBooks(books)

                // save the cart
                val savedcart = cartRepository.save(cart)
                print("saved : " + savedcart.javaClass.toString())
                println()
                print("rand : " + (redisTemplate.opsForSet().randomMember(Cart::class.java.name)).toString())
                println()
                // randomly checkout carts
                if (random.nextBoolean()) {
                    cartService.checkout(cart.id)
                }
            }
            println(">>>> Created Carts...")
        }
    }

    private fun getRandomBooks(bookRepository: BookRepository?, max: Int): Set<Book> {
        val random = Random()
        val howMany = random.nextInt(max) + 1
        val books: MutableSet<Book> = HashSet()
        IntStream.range(0, howMany).forEach {
            val randomBookId = redisTemplate.opsForSet().randomMember(Book::class.java.name)
            books.add(bookRepository!!.findById(randomBookId).get())
        }
        return books
    }

    private fun getCartItemsForBooks(books: Set<Book>): Set<CartItem> {
        val items: MutableSet<CartItem> = HashSet()
        books.forEach(Consumer { book: Book ->
            val item = CartItem()
                item.isbn = book.id
                item.price = book.price
                item.quantity= 1L
            items.add(item)
        })
        return items
    }
}