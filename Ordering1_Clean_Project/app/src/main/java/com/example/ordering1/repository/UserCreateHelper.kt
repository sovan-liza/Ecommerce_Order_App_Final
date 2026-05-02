package com.example.ordering1.repository

import com.google.firebase.database.FirebaseDatabase

/**
 * Creates a user node in Firebase Realtime DB on first phone login.
 * Called from OTPFragment after successful OTP verification.
 * Path: "Users/{uid}"
 */
class UserCreateHelper {

    private val ref = FirebaseDatabase.getInstance().getReference("Users")

    fun createIfNotExists(uid: String, phone: String) {
        ref.child(uid).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                val map = hashMapOf(
                    "uid" to uid,
                    "userName" to "New User",
                    "userPhoneNumber" to phone,
                    "userAddress" to "",
                    "profileImage" to "",
                    "userType" to "user"
                )
                ref.child(uid).setValue(map)
            }
        }
    }
}