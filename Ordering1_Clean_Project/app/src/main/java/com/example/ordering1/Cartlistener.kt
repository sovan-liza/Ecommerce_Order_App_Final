package com.example.ordering1

interface CartListener {
    fun showCartLayout(itemCount : Int)
    fun savingCartItemCount(itemCount: Int)

}