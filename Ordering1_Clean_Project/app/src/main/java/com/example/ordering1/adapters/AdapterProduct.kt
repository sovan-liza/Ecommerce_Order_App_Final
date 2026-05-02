package com.example.ordering1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ordering1.FilteringProducts
import com.example.ordering1.databinding.ItemViewProductBinding
import com.example.ordering1.model.Product
import kotlin.apply
import kotlin.ranges.until
import kotlin.toString


class AdapterProduct(
    val onAddButtonCliked: (Product, ItemViewProductBinding) -> Unit,
    val onIncrementButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onDecrementButtonClicked: (Product, ItemViewProductBinding) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(), Filterable{

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
            val imageList= kotlin.collections.ArrayList<SlideModel>()
            val productImagee = product.productImageUris

            for (i in 0 until productImagee?.size!!){
                imageList.add(SlideModel(product.productImageUris!![i].toString()))
            }
            ivImageSlider.setImageList(imageList)
            tvProductTitle.text=product.productTitle
            val quantity = product.productQuantity.toString() + product.productUnit
            tvProductQuantity.text=quantity
            tvProductPrice.text = "$" + product.productPrice
            if(product.itemCount!! > 0){
                tvProductCount.text = product.itemCount.toString()
                tvAdd.visibility = View.GONE
                llProductCount.visibility = View.VISIBLE
            }
            tvAdd.setOnClickListener {
                onAddButtonCliked(product,this)
            }
            tvIncrementCount.setOnClickListener {
                onIncrementButtonClicked(product,this)
            }
            tvDecrementCount.setOnClickListener {
                onDecrementButtonClicked(product,this)
            }

        }

    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var orginalList = kotlin.collections.ArrayList<Product>()

    private lateinit var productFilter: FilteringProducts

    override fun getFilter(): Filter {
        if (!::productFilter.isInitialized) {
            productFilter = FilteringProducts(this, orginalList)
        }
        return productFilter
    }
}