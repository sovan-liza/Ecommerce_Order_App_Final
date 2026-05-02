package com.example.ordering1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.ordering1.databinding.ItemViewCartProductsBinding
import com.example.ordering1.roomdb.CartProducts

class AdapterCartProducts :
    RecyclerView.Adapter<AdapterCartProducts.CartProductsViewHolder>() {

    class CartProductsViewHolder(
        val binding: ItemViewCartProductsBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<CartProducts>() {
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts) =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts) =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            ItemViewCartProductsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val product = differ.currentList[position]

        holder.binding.apply {

            Glide.with(root.context)
                .load(product.productImage)
                .into(ivProductImage)

            tvProductTitle.text = product.productTitle
            tvProductQuantity.text = "${product.productQuantity} ${product.productUnit}"
            tvProductPrice.text = "$${product.productPrice}"
            tvProductCount.text = product.productCount.toString()

            // ✅ ITEM TOTAL
            tvItemTotal.text = "$${product.getItemTotal()}"
        }
    }

    override fun getItemCount() = differ.currentList.size
}