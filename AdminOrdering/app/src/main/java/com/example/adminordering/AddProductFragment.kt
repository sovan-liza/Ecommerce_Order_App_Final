package com.example.adminordering

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminordering.adapter.AdapterSelectedImage
import com.example.adminordering.databinding.FragmentAddProductBinding
import com.example.adminordering.model.Product
import com.example.adminordering.viewmodels.AdminViewModel
import kotlinx.coroutines.launch


class AddProductFragment : Fragment() {
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentAddProductBinding
    private val imageUris: ArrayList<Uri> = arrayListOf()
    val selectedImage =  registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listOfUri ->
        val fiveImage = listOfUri.take(5)
        imageUris.clear()
        imageUris.addAll(fiveImage)
        binding.rvProductImage.adapter = AdapterSelectedImage(imageUris)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddProductBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAutoCompleteTextViews()
        onImageSelectClicked()
        onAddButtonClicked()
        return binding.root
    }

    private fun onAddButtonClicked() {
        binding.btnAddProduct.setOnClickListener {
            Utils.showDialog(requireContext(),"Uploading images...")
            val productTitle=binding.etProductTitle.text.toString()
            val productQuantity=binding.etQuantity.text.toString()
            val productUnit=binding.etProductUnit.text.toString()
            val productPrice=binding.etProductPrice.text.toString()
            val productStock=binding.etNoProduct.text.toString()
            val productCategory=binding.etProductCategory.text.toString()
            val productType=binding.etProductType.text.toString()

            if (productTitle.isEmpty() || productQuantity.isEmpty() || productUnit.isEmpty()
                || productPrice.isEmpty() || productStock.isEmpty() || productCategory.isEmpty() || productType.isEmpty() ){
                Utils.apply {
                   hideDialog()
                   showToast(requireContext(),"Empty fields are not allowed")
                }

            }
            else if (imageUris.isEmpty()){
                Utils.apply {
                    hideDialog()
                    showToast(requireContext(),"Please upload some images")
                }
            }
            else{
                val product = Product(
                    productTitle= productTitle,
                    productQuantity = productQuantity.toInt(),
                    productUnit = productUnit,
                    productPrice = productPrice.toInt(),
                    productStock = productStock.toInt(),
                    productCategory = productCategory,
                    productType = productType,
                    itemCount = 0,
                    adminUid= Utils.getCurrentUserId(),
                    productRandomId= Utils.getRandomId(),
                )
                saveImage(product)
            }
        }
    }

    private fun saveImage(product: Product) {
        // Call the updated method
        viewModel.uploadImagesToCloudinary(imageUris)

        lifecycleScope.launch {
            viewModel.isImagesUploaded.collect { uploaded ->
                if (uploaded) {
                    Utils.apply {
                        hideDialog()
                        showToast(requireContext(), "Image saved")
                    }
                    getUrls(product)
                }
            }
        }
    }

    private fun getUrls(product: Product) {
        Utils.showDialog(requireContext(),"Publishing product....")
        lifecycleScope.launch {
            viewModel.downloadUrls.collect {
                val urls = it
                product.productImageUris = ArrayList(urls) // make sure types match
                saveProduct(product) // now product stores Cloudinary URLs
            }
        }
    }

    private fun saveProduct(product: Product) {
         viewModel.saveProduct(product)
        lifecycleScope.launch {
            viewModel.isProductSaved.collect {
                if (it){
                    Utils.hideDialog()
                    startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                    Utils.showToast(requireContext(),"Your product is live")
                }
            }
        }
    }

    private fun onImageSelectClicked() {
        binding.btnselectImage.setOnClickListener {
            selectedImage.launch("image/*")
        }
    }

    private fun setAutoCompleteTextViews() {
      val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProducts)
        val category= ArrayAdapter(requireContext(),R.layout.show_list, Constants.allProductsCatagory)
        val productType= ArrayAdapter(requireContext(),R.layout.show_list, Constants.allProductType)
        binding.apply {
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


