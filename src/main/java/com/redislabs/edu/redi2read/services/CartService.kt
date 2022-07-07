package com.redislabs.edu.redi2read.services

import com.redislabs.edu.redi2read.models.Cart
import com.redislabs.edu.redi2read.models.CartItem
import com.redislabs.edu.redi2read.repositories.BookRepository
import com.redislabs.edu.redi2read.repositories.CartRepository
import com.redislabs.edu.redi2read.repositories.UserRepository
import com.redislabs.modules.rejson.JReJSON
import com.redislabs.modules.rejson.Path
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.function.Consumer
import java.util.stream.LongStream

@Service
class CartService {
    @Autowired
    private var cartRepository: CartRepository? = null

    @Autowired
    private var bookRepository: BookRepository? = null

    @Autowired
    private var userRepository: UserRepository? = null
    private var redisJson = JReJSON()
    var cartItemsPath = Path.of(".cartItems")
    operator fun get(id: String?): Cart {
        return cartRepository!!.findById(id).get()
    }

    fun addToCart(id: String?, item: CartItem) {
        var book = bookRepository!!.findById(item.isbn)
        if (book.isPresent) {
            var cartKey: String = CartRepository.Companion.getKey(id)
            item.price = book.get().price
            redisJson.arrAppend(cartKey, cartItemsPath, item)
        }
    }

    fun removeFromCart(id: String?, isbn: String) {
        var cartFinder = cartRepository!!.findById(id)
        if (cartFinder.isPresent) {
            var cart = cartFinder.get()
            var cartKey: String = CartRepository.Companion.getKey(cart.id)
            var cartItems: List<CartItem> = ArrayList(cart.cartItems)
            var cartItemIndex =
                LongStream.range(0, cartItems.size.toLong()).filter { i: Long -> cartItems[i.toInt()].isbn == isbn }
                    .findFirst()
            if (cartItemIndex.isPresent) {
                redisJson.arrPop(cartKey, CartItem::class.java, cartItemsPath, cartItemIndex.asLong)
            }
        }
    }

    fun checkout(id: String?) {
        var cart = cartRepository!!.findById(id).get()
        var user = userRepository!!.findById(cart.userId).get()
        cart.cartItems?.forEach(Consumer { cartItem: CartItem ->
            var book = bookRepository!!.findById(cartItem.isbn).get()
            user.addBook(book)
        })
        userRepository!!.save(user)
        // cartRepository.delete(cart);
    }
}