package com.example.ordering1.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.ordering1.Utils
import com.example.ordering1.model.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId = _verificationId

    val otpSent = MutableStateFlow(false)
    val isSignedInSuccessfully = MutableStateFlow(false)

    // ================= OTP SEND =================
    fun sendOTP(userNumber: String, activity: Activity) {

        otpSent.value = false
        _verificationId.value = null

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                otpSent.value = false
                Utils.showToast(activity, "OTP Failed: ${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                _verificationId.value = verificationId
                otpSent.value = true
            }
        }

        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber(userNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // ================= SIGN IN =================
    fun signInWithPhoneAuthCredential(
        otp: String,
        userNumber: String,
        user: Users
    ) {

        val verificationId = _verificationId.value ?: run {
            isSignedInSuccessfully.value = false
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, otp)

        Utils.getAuthInstance()
            .signInWithCredential(credential)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    isSignedInSuccessfully.value = false
                    return@addOnCompleteListener
                }

                val firebaseUser = Utils.getAuthInstance().currentUser

                if (firebaseUser == null) {
                    isSignedInSuccessfully.value = false
                    return@addOnCompleteListener
                }

                val uid = firebaseUser.uid
                user.uid = uid

                // ✅ FIXED DATABASE PATH (IMPORTANT)
                FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(uid)
                    .setValue(user)
                    .addOnSuccessListener {
                        isSignedInSuccessfully.value = true
                    }
                    .addOnFailureListener {
                        isSignedInSuccessfully.value = false
                    }
            }
    }
}