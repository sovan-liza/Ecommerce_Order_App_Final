package com.example.adminordering.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.adminordering.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class AdminViewModel : ViewModel() {

    private val _isImagesUploaded = MutableStateFlow(false)
    val isImagesUploaded: StateFlow<Boolean> = _isImagesUploaded

    private val _downloadedUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    val downloadUrls: StateFlow<ArrayList<String?>> = _downloadedUrls

    private val _isProductSaved=MutableStateFlow(false)
    var isProductSaved: StateFlow<Boolean> =  _isProductSaved


    fun uploadImagesToCloudinary(imageUris: ArrayList<Uri>) {
        if (imageUris.isEmpty()) return

        val uploadedUrls = ArrayList<String?>()

        imageUris.forEach { uri ->

            val uniqueId = UUID.randomUUID().toString()
            val publicId = "users/$uniqueId"

            MediaManager.get().upload(uri)
                .option("public_id", publicId)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val secureUrl = resultData?.get("secure_url")?.toString()
                        uploadedUrls.add(secureUrl)
                        checkAllUploadsComplete(uploadedUrls, imageUris.size)
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        uploadedUrls.add(null)
                        checkAllUploadsComplete(uploadedUrls, imageUris.size)
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        }
    }

    fun saveProduct (product : Product){
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("AllProducts/${product.productRandomId}").setValue(product)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins")
                    .child("ProductCategory/${product.productCategory}/${product.productRandomId}").setValue(product)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins")
                            .child("ProductType/${product.productType}/${product.productRandomId}").setValue(product)
                            .addOnSuccessListener {
                                _isProductSaved.value=true
                            }
                    }
            }
    }

    fun fetchAllTheProducts(category: String): Flow<List<Product>> = callbackFlow{
        val db= FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")
        val eventListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()

                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    if (category == "All" || prod?.productCategory == category){
                        products.add(prod!!)
                    }

                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }
 

    private fun checkAllUploadsComplete(uploadedUrls: ArrayList<String?>, total: Int) {
        if (uploadedUrls.size == total) {
            _downloadedUrls.value = uploadedUrls
            _isImagesUploaded.value = uploadedUrls.all { it != null }
        }
    }
    fun savingUpdateProducts(product: Product){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productRandomId}").setValue(product)
    }


}