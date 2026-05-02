package com.example.ordering1.model

data class Users(
    var uid: String? = null,
    var userName: String? = null,
    var userPhoneNumber: String? = null,
    var userAddress: String? = null,
    var profileImage: String? = null,
    var userType: String? = "user"
)