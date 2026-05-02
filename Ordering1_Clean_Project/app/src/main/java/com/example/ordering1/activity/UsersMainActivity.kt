package com.example.ordering1.activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ordering1.CartListener
import com.example.ordering1.adapters.AdapterCartProducts
import com.example.ordering1.databinding.ActivityUsersMainBinding
import com.example.ordering1.databinding.BsCartProductsBinding
import com.example.ordering1.roomdb.CartProducts
import com.example.ordering1.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.getValue

class UsersMainActivity : AppCompatActivity(), CartListener {

    private lateinit var binding: ActivityUsersMainBinding
    private val viewModel: UserViewModel by viewModels()

    private lateinit var cartProductList: List<CartProducts>
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTotalItemCountInCart()
        getAllCartProducts()
        onNexButtonClicked()
        onCartClicked()
    }

    private fun onNexButtonClicked() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }

    private fun getAllCartProducts() {
        viewModel.getAll().observe(this) {
            cartProductList = it
        }
    }

    private fun onCartClicked() {
        binding.llItemCart.setOnClickListener {

            val bsCartProductsBinding =
                BsCartProductsBinding.inflate(LayoutInflater.from(this))

            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductsBinding.root)

            bsCartProductsBinding.tvNumberOfProductCount.text =
                binding.tvNumberOfProductCount.text

            bsCartProductsBinding.btnNext.setOnClickListener {
                startActivity(Intent(this, OrderPlaceActivity::class.java))
            }

            adapterCartProducts = AdapterCartProducts()
            bsCartProductsBinding.rvProductsItems.adapter = adapterCartProducts

            adapterCartProducts.differ.submitList(cartProductList)

            bs.show()
        }
    }

    private fun getTotalItemCountInCart() {
        // FIX HERE
        viewModel.getTotalCartItemCount().observe(this) {

            if (it > 0) {
                binding.llCart.visibility = View.VISIBLE
                binding.tvNumberOfProductCount.text = it.toString()
            } else {
                binding.llCart.visibility = View.GONE
                binding.tvNumberOfProductCount.text = "0"
            }
        }
    }

    override fun showCartLayout(itemCount: Int) {
        val previousCount =
            binding.tvNumberOfProductCount.text.toString().toIntOrNull() ?: 0

        val updatedCount = previousCount + itemCount

        if (updatedCount > 0) {
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        } else {
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }
    }

    override fun savingCartItemCount(itemCount: Int) {
        // NO NEED anymore because Room handles count automatically
        // Keep empty to avoid crash
    }
}