package com.example.ordering1.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ordering1.model.Product
import com.example.ordering1.roomdb.CartProductDao
import com.example.ordering1.roomdb.CartProducts
import com.example.ordering1.roomdb.CartProductsDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("My_Pref", MODE_PRIVATE)

    private val cartDao =
        CartProductsDatabase.getDatabaseInstance(application).cartProductsDao()

    // ================= CART =================

    fun insertCartProduct(product: CartProducts) = viewModelScope.launch {
        cartDao.insertCartProduct(product)
    }

    fun getAll(): LiveData<List<CartProducts>> {
        return cartDao.getAllCartProducts()
    }

    fun deleteCartProduct(productId: String) = viewModelScope.launch {
        cartDao.deleteCartProductById(productId)
    }

    fun clearCart() = viewModelScope.launch {
        cartDao.clearCart()
    }

    // 🔥 ONLY SOURCE OF TRUTH FOR BADGE
    fun getTotalCartItemCount(): LiveData<Int> {
        return cartDao.getTotalCartItemCount()
    }

    // ================= PRODUCTS =================

    fun fetchAllTheProducts(): Flow<List<Product>> = callbackFlow {

        val ref = FirebaseDatabase.getInstance()
            .getReference("Admins")
            .child("AllProducts")

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val list = snapshot.children.mapNotNull {
                    it.getValue(Product::class.java)
                }

                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose { ref.removeEventListener(listener) }
    }

    fun getCategoryProduct(category: String): Flow<List<Product>> = callbackFlow {

        val ref = FirebaseDatabase.getInstance()
            .getReference("Admins")
            .child("ProductCategory")
            .child(category.trim())

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val list = snapshot.children.mapNotNull {
                    it.getValue(Product::class.java)
                }

                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose { ref.removeEventListener(listener) }
    }

    // ================= FIREBASE UPDATE =================

    fun updateItemCount(product: Product, itemCount: Int) {

        val id = product.productRandomId ?: return
        val category = product.productCategory ?: return
        val type = product.productType ?: return

        val updates = hashMapOf<String, Any>(
            "/Admins/AllProducts/$id/itemCount" to itemCount,
            "/Admins/ProductCategory/$category/$id/itemCount" to itemCount,
            "/Admins/ProductType/$type/$id/itemCount" to itemCount
        )

        FirebaseDatabase.getInstance()
            .reference
            .updateChildren(updates)
    }

    // ================= ADDRESS =================

    fun saveUserAddress(address: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("AllUsers")
            .child("Users")
            .child(uid)
            .child("userAddress")
            .setValue(address)
    }

    fun saveAddressStatus() {
        sharedPreferences.edit()
            .putBoolean("addressStatus", true)
            .apply()
    }

    fun getAddressStatus(): LiveData<Boolean> {
        val live = MutableLiveData<Boolean>()
        live.value = sharedPreferences.getBoolean("addressStatus", false)
        return live
    }
}