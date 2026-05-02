package com.example.ordering1.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CartProductDao {

    @Query("SELECT * FROM cart_products")
    fun getAllCartProducts(): LiveData<List<CartProducts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartProduct(product: CartProducts)

    @Query("DELETE FROM cart_products WHERE productId = :productId")
    suspend fun deleteCartProductById(productId: String)

    @Query("DELETE FROM cart_products")
    suspend fun clearCart()

    // ✅ SAFE TOTAL (never null)
    @Query("SELECT IFNULL(SUM(productPrice * productCount), 0) FROM cart_products")
    fun getTotalPrice(): LiveData<Double>

    // ✅ SAFE COUNT (never null)
    @Query("SELECT IFNULL(SUM(productCount), 0) FROM cart_products")
    fun getTotalCartItemCount(): LiveData<Int>
}