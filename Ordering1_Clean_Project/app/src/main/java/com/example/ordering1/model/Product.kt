package com.example.ordering1.model

data class Product(
    var productRandomId: String = "",
    var productTitle: String = "",
    var productQuantity: Int = 0,
    var productUnit: String = "",

    // ✅ FIXED (NON NULL SAFE FOR MATH)
    var productPrice: Double = 0.0,

    var productStock: Int = 0,
    var productCategory: String = "",
    var productType: String = "",
    var itemCount: Int = 0,
    var adminUid: String = "",
    var productImageUris: ArrayList<String> = arrayListOf()
)