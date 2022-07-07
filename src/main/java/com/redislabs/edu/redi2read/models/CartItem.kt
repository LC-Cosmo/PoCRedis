package com.redislabs.edu.redi2read.models

import lombok.Builder
import lombok.Data

@Data
@Builder
class CartItem {
    var isbn: String? = null
    var price: Double? = null
    var quantity: Long? = null
}