package com.example.ordering1.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ordering1.CartListener
import com.example.ordering1.R
import com.example.ordering1.Utils
import com.example.ordering1.adapters.AdapterProduct
import com.example.ordering1.databinding.FragmentSearchBinding
import com.example.ordering1.databinding.ItemViewProductBinding
import com.example.ordering1.model.Product
import com.example.ordering1.roomdb.CartProducts
import com.example.ordering1.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapterProduct: AdapterProduct
    private var cartListener: CartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupAdapter()
        getAllTheProducts()
        searchProducts()

        return binding.root
    }

    private fun setupAdapter() {
        adapterProduct = AdapterProduct(
            ::onAddButtonClicked,
            ::onIncrementButtonClicked,
            ::onDecrementButtonClicked
        )

        binding.rvProducts.adapter = adapterProduct
    }

    private fun searchProducts() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                adapterProduct.filter.filter(s.toString().trim())
            }
        })
    }

    private fun getAllTheProducts() {
        binding.shimmerViewContainer.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.fetchAllTheProducts().collect { productList ->

                binding.shimmerViewContainer.visibility = View.GONE

                if (productList.isEmpty()) {
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }

                adapterProduct.orginalList = ArrayList(productList)
                adapterProduct.differ.submitList(productList)
            }
        }
    }

    private fun onAddButtonClicked(
        product: Product,
        productBinding: ItemViewProductBinding
    ) {
        val id = product.productRandomId ?: return

        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE

        val newCount = 1
        productBinding.tvProductCount.text = newCount.toString()
        product.itemCount = newCount

        cartListener?.showCartLayout(1)
        cartListener?.savingCartItemCount(1)

        lifecycleScope.launch {
            viewModel.updateItemCount(product, newCount)
            saveProductInRoomDb(product)
        }
    }

    private fun onIncrementButtonClicked(
        product: Product,
        productBinding: ItemViewProductBinding
    ) {
        val current = productBinding.tvProductCount.text.toString().toIntOrNull() ?: 0
        val stock = product.productStock ?: Int.MAX_VALUE

        if (current >= stock) {
            Utils.showToast(requireContext(), "Can't add more item of this")
            return
        }

        val newCount = current + 1

        productBinding.tvProductCount.text = newCount.toString()
        product.itemCount = newCount

        cartListener?.showCartLayout(1)
        cartListener?.savingCartItemCount(1)

        lifecycleScope.launch {
            viewModel.updateItemCount(product, newCount)
            saveProductInRoomDb(product)
        }
    }

    private fun onDecrementButtonClicked(
        product: Product,
        productBinding: ItemViewProductBinding
    ) {
        val current = productBinding.tvProductCount.text.toString().toIntOrNull() ?: 0
        if (current <= 0) return

        val newCount = current - 1
        product.itemCount = newCount

        cartListener?.showCartLayout(-1)
        cartListener?.savingCartItemCount(-1)

        lifecycleScope.launch {
            if (newCount > 0) {
                viewModel.updateItemCount(product, newCount)
                saveProductInRoomDb(product)
            } else {
                product.productRandomId?.let {
                    viewModel.deleteCartProduct(it)
                }
            }
        }

        if (newCount > 0) {
            productBinding.tvProductCount.text = newCount.toString()
        } else {
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvProductCount.text = "0"
        }
    }

    private fun saveProductInRoomDb(product: Product) {

        val firstImage = product.productImageUris?.firstOrNull() ?: ""

        val cartProduct = CartProducts(
            productId = product.productRandomId ?: return,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity ?: 0,
            productUnit = product.productUnit,
            productPrice = product.productPrice ?: 0.0,
            productCount = product.itemCount ?: 0,
            productStock = product.productStock,
            productImage = firstImage,
            productCategory = product.productCategory,
            adminUid = product.adminUid
        )

        lifecycleScope.launch {
            viewModel.insertCartProduct(cartProduct)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cartListener = context as? CartListener
            ?: throw ClassCastException("Activity must implement CartListener")
    }

    override fun onDetach() {
        super.onDetach()
        cartListener = null
    }
}