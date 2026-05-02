package com.example.ordering1.repository

import com.example.ordering1.roomdb.CartProductDao
import com.example.ordering1.roomdb.CartProducts

class CartRepository(private val dao: CartProductDao) {

    suspend fun insertCartProduct(cartProducts: CartProducts) {
        dao.insertCartProduct(cartProducts)
    }

    fun getAllCartProducts() = dao.getAllCartProducts()

    fun getTotalCartItemCount() = dao.getTotalCartItemCount()
}