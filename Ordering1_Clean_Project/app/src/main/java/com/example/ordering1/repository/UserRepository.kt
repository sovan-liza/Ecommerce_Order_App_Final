package com.example.ordering1.repository

import com.example.ordering1.roomdb.CartProductDao

class UserRepository(private val cartProductDao: CartProductDao) {

    suspend fun clearCart() {
        cartProductDao.clearCart()
    }

    // (you can also add other DB functions here later)
}