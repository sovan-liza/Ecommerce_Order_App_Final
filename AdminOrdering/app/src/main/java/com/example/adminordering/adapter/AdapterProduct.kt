package com.example.adminordering.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.adminordering.FilteringProducts
import com.example.adminordering.databinding.ItemViewProductBinding
import com.example.adminordering.model.Product


class AdapterProduct(
    val onEditButtonClicked: (Product) -> Unit) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(),Filterable {

    class ProductViewHolder(val binding: ItemViewProductBinding): RecyclerView.ViewHolder(binding.root){


    }

    val diffutil = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
           return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
           return  oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffutil)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
       return ProductViewHolder(ItemViewProductBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val product=differ.currentList[position]
        holder.binding.apply {
            val imageList= ArrayList<SlideModel>()
            val productImagee = product.productImageUris

            for (i in 0 until productImagee?.size!!){
                imageList.add(SlideModel(product.productImageUris!![i].toString()))
            }
            ivImageSlider.setImageList(imageList)
            tvProductTitle.text=product.productTitle
            val quantity = product.productQuantity.toString() + product.productUnit
            tvProductQuantity.text=quantity
            tvProductPrice.text = "$" + product.productPrice

        }
        holder.itemView.setOnClickListener {
            onEditButtonClicked(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var orginalList = ArrayList<Product>()

    private lateinit var productFilter: FilteringProducts

    override fun getFilter(): Filter {
        if (!::productFilter.isInitialized) {
            productFilter = FilteringProducts(this, orginalList)
        }
        return productFilter
    }


}