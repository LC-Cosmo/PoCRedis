package com.redislabs.edu.redi2read.models

import lombok.*
import lombok.experimental.Accessors

@Accessors(fluent = true)
@Data
@Builder
class Cart {

    @Setter
    var id: String? = null
    var userId: String? = null

    @Singular
    var cartItems: Set<CartItem>? = null
    fun count(): Int {
        return cartItems!!.size
    }

    var total: Double? = null
        get() = cartItems //
            ?.stream() //
            ?.mapToDouble { ci: CartItem -> ci.price!! * ci.quantity!! } //
            ?.sum()
}