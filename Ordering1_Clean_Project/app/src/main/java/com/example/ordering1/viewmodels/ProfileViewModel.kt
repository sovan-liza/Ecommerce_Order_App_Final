package com.example.ordering1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ordering1.model.Users
import com.google.firebase.database.*

class ProfileViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().getReference("Users")

    private val _userData = MutableLiveData<Users>()
    val userData: LiveData<Users> = _userData

    fun loadUser(uid: String) {

        dbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(Users::class.java)
                _userData.postValue(user ?: Users(uid = uid))
            }

            override fun onCancelled(error: DatabaseError) {
                _userData.postValue(Users(uid = uid))
            }
        })
    }
}