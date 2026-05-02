package com.example.ordering1.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_products")
data class CartProducts(

    @PrimaryKey
    val productId: String,

    var productTitle: String? = null,
    var productQuantity: Int = 0,
    var productUnit: String? = null,
    var productPrice: Double = 0.0,
    var productImage: String? = null,
    var productStock: Int? = null,
    var productCount: Int = 0,
    var productCategory: String? = null,
    var adminUid: String? = null
) {
    fun getItemTotal(): Double {
        return productPrice * productCount
    }
}