package com.example.adminordering

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminordering.adapter.AdapterProduct
import com.example.adminordering.adapter.CategoriesAdapter
import com.example.adminordering.databinding.EditProductLayoutBinding
import com.example.adminordering.databinding.FragmentHomeBinding
import com.example.adminordering.model.Categories
import com.example.adminordering.model.Product
import com.example.adminordering.viewmodels.AdminViewModel
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    val viewModel : AdminViewModel by viewModels()
   private lateinit var binding: FragmentHomeBinding
   private lateinit var adapterProduct : AdapterProduct
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setCategories()
        searchProducts()
        getAllTheProducts("All")
        return binding.root
    }

    private fun searchProducts() {
        binding.searchEt.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val query = s.toString().trim()
                adapterProduct?.filter?.filter(query)
            }

        })
    }

    private fun getAllTheProducts(category: String) {
        binding.shimmerViewContainer.visibility= View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllTheProducts(category).collect{
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
                else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(::onEditButtonClicked)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.orginalList = it as ArrayList<Product>
                binding.shimmerViewContainer.visibility= View.GONE
            }
        }
    }

    private fun setCategories() {
        val categoryList= ArrayList<Categories>()
        for (i in 0 until Constants.allProductsCategoryIcon.size){
            categoryList.add(Categories(Constants.allProductsCatagory[i], Constants.allProductsCategoryIcon[i]))
        }
        binding.rvCategories.adapter= CategoriesAdapter(categoryList,::onCategoryClicked)
    }
    private  fun onCategoryClicked(categories: Categories){
        getAllTheProducts(categories.category)
    }
    private fun onEditButtonClicked(product : Product){
        val editProduct = EditProductLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        editProduct.apply {
           etProductTitle.setText(product.productTitle)
            etQuantity.setText(product.productQuantity.toString())
            etProductUnit.setText(product.productUnit)
            etProductPrice.setText(product.productPrice.toString())
            etNoProduct.setText(product.productStock.toString())
            etProductCategory.setText(product.productCategory)
            etProductType.setText(product.productType)
        }
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProduct.root)
            .create()
        alertDialog.show()

        editProduct.btnEdit.setOnClickListener {
            editProduct.apply {
                etProductTitle.isEnabled = true
                etQuantity.isEnabled=true
                etProductUnit.isEnabled=true
                etProductPrice.isEnabled=true
                etNoProduct.isEnabled=true
                etProductCategory.isEnabled=true
                etProductType.isEnabled=true


            }

            
        }
        setAutoCompleteTextViews(editProduct)
        editProduct.btnSave.setOnClickListener {
            lifecycleScope.launch {
                product.productTitle = editProduct.etProductTitle.text.toString()
                product.productQuantity = editProduct.etQuantity.text.toString().toInt()
                product.productUnit = editProduct.etProductUnit.text.toString()
                product.productPrice = editProduct.etProductPrice.text.toString().toInt()
                product.productStock = editProduct.etNoProduct.text.toString().toInt()
                product.productCategory = editProduct.etProductCategory.text.toString()
                product.productType = editProduct.etProductType.text.toString()
                viewModel.savingUpdateProducts(product)
            }
            Utils.showToast(requireContext(),"Saved changes!")
            alertDialog.dismiss()

        }
    }

    private fun setAutoCompleteTextViews(editProduct: EditProductLayoutBinding) {
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProducts)
        val category= ArrayAdapter(requireContext(),R.layout.show_list, Constants.allProductsCatagory)
        val productType= ArrayAdapter(requireContext(),R.layout.show_list, Constants.allProductType)
        editProduct.apply {
            etProductUnit.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(productType)

        }
    }


    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors= ContextCompat.getColor(requireContext(), R.color.dar_yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}