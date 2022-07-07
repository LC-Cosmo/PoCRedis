package com.redislabs.edu.redi2read.controllers

import com.redislabs.edu.redi2read.models.Cart
import com.redislabs.edu.redi2read.models.CartItem
import com.redislabs.edu.redi2read.services.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/carts")
class CartController {
    @Autowired
    private var cartService: CartService? = null

    @GetMapping("/{id}")
    operator fun get(@PathVariable("id") id: String?): Cart {
        return cartService!![id]
    }

    @PostMapping("/{id}")
    fun addToCart(@PathVariable("id") id: String?, @RequestBody item: CartItem?) {
        if (item != null) {
            cartService!!.addToCart(id, item)
        }
    }

    @DeleteMapping("/{id}")
    fun removeFromCart(@PathVariable("id") id: String?, @RequestBody isbn: String?) {
        if (isbn != null) {
            cartService!!.removeFromCart(id, isbn)
        }
    }

    @PostMapping("/{id}/checkout")
    fun checkout(@PathVariable("id") id: String?) {
        cartService!!.checkout(id)
    }
}