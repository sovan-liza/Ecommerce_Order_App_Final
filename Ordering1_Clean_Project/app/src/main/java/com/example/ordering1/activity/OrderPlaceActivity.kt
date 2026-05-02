package com.example.ordering1.activity

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ordering1.Constatns
import com.example.ordering1.R
import com.example.ordering1.adapters.AdapterCartProducts
import com.example.ordering1.databinding.ActivityOrderPlaceBinding
import com.example.ordering1.databinding.AddressLayoutBinding
import com.example.ordering1.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject

class OrderPlaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderPlaceBinding
    private lateinit var viewModel: UserViewModel

    private var adapterCartProducts: AdapterCartProducts? = null

    private var hasAddress = false
    private var savedAddress = ""
    private var grandTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        setStatusBarColor()
        backToMain()

        loadAddressFromFirebase()   // ✅ FIX
        getAllCartProducts()
        observeAddressStatus()

        onPlaceOrderClicked()
    }

    // ================= LOAD ADDRESS =================
    private fun loadAddressFromFirebase() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("AllUsers")
            .child("Users")
            .child(uid)
            .child("userAddress")
            .get()
            .addOnSuccessListener {

                val address = it.getValue(String::class.java)

                if (!address.isNullOrEmpty()) {
                    savedAddress = address
                    hasAddress = true
                }
            }
    }

    // ================= PLACE ORDER CLICK =================
    private fun onPlaceOrderClicked() {
        binding.btnPlaceOrder.setOnClickListener {
            if (!hasAddress || savedAddress.isEmpty()) {
                showAddressDialog()
            } else {
                showKhqrDialog()
            }
        }
    }

    // ================= ADDRESS DIALOG =================
    private fun showAddressDialog() {

        val addressBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

        val dialog = AlertDialog.Builder(this)
            .setView(addressBinding.root)
            .create()

        dialog.show()

        addressBinding.btnAdd.setOnClickListener {

            val pin = addressBinding.etPinCode.text.toString().trim()
            val phone = addressBinding.etPhoneNo.text.toString().trim()
            val state = addressBinding.etState.text.toString().trim()
            val district = addressBinding.etDistrict.text.toString().trim()
            val desc = addressBinding.etDescriptiveAddress.text.toString().trim()

            if (pin.isEmpty() || phone.isEmpty() ||
                state.isEmpty() || district.isEmpty() || desc.isEmpty()
            ) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            savedAddress = "$pin,$district($state),$desc,$phone"

            val uid = FirebaseAuth.getInstance().currentUser?.uid

            if (uid != null) {
                FirebaseDatabase.getInstance()
                    .getReference("AllUsers")
                    .child("Users")
                    .child(uid)
                    .child("userAddress")
                    .setValue(savedAddress)
            }

            hasAddress = true

            Toast.makeText(this, "Address Saved", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    // ================= KHQR DIALOG =================
    private fun showKhqrDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_khqr_payment)

        val imgQR = dialog.findViewById<ImageView>(R.id.imgQR)
        val tvAmount = dialog.findViewById<TextView>(R.id.txtAmount)
        val btnPaid = dialog.findViewById<Button>(R.id.btnPaid)

        imgQR.setImageResource(R.drawable.qr)
        tvAmount.text = "Pay Amount: $$grandTotal"

        btnPaid.setOnClickListener {
            placeOrder()
            dialog.dismiss()
        }

        dialog.show()
    }

    // ================= PLACE ORDER =================
    private fun placeOrder() {

        val user = FirebaseAuth.getInstance().currentUser ?: return

        val orderId = "ORD-${System.currentTimeMillis()}"

        val orderMap = hashMapOf(
            "orderId" to orderId,
            "userId" to user.uid,
            "amount" to grandTotal,
            "address" to savedAddress,
            "paymentMethod" to "KHQR",
            "status" to "Pending Confirmation",
            "time" to System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance()
            .getReference("Orders")
            .child(orderId)
            .setValue(orderMap)
            .addOnSuccessListener {

                sendTelegramMessage(orderId, user.uid)

                // ✅ CLEAR CART HERE
                viewModel.clearCart()

                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, UsersMainActivity::class.java))
                finish()
            }
    }

    // ================= TELEGRAM =================
    private fun sendTelegramMessage(orderId: String, uid: String) {

        val message = """
🛒 NEW ORDER

📦 Order ID: $orderId
👤 User: $uid
💵 Amount: $$grandTotal
📍 Address: $savedAddress
💳 Payment: KHQR
📢 Status: Pending Confirmation
        """.trimIndent()

        val url = "https://api.telegram.org/bot${Constatns.TELEGRAM_BOT_TOKEN}/sendMessage"

        val json = JSONObject().apply {
            put("chat_id", Constatns.TELEGRAM_CHAT_ID)
            put("text", message)
        }

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            json,
            { },
            { it.printStackTrace() }
        )

        Volley.newRequestQueue(this).add(request)
    }

    // ================= CART =================
    private fun getAllCartProducts() {

        viewModel.getAll().observe(this) { list ->

            if (adapterCartProducts == null) {
                adapterCartProducts = AdapterCartProducts()
                binding.rvProductsItems.adapter = adapterCartProducts
            }

            adapterCartProducts?.differ?.submitList(list)

            var total = 0.0

            for (item in list) {
                total += item.productPrice * item.productCount
            }

            grandTotal = total
            binding.tvGrandTotal.text = "Total: $$grandTotal"
        }
    }

    // ================= ADDRESS STATUS =================
    private fun observeAddressStatus() {
        viewModel.getAddressStatus().observe(this) {
            hasAddress = it
        }
    }

    // ================= UI =================
    private fun setStatusBarColor() {
        window.statusBarColor =
            ContextCompat.getColor(this, R.color.dar_yellow)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun backToMain() {
        binding.tbOrderFragment.setNavigationOnClickListener {
            startActivity(Intent(this, UsersMainActivity::class.java))
            finish()
        }
    }
}