package com.example.ordering1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ordering1.databinding.ItemViewProductCategoryBinding
import com.example.ordering1.model.Category

class AdapterCategory(val categoryList: ArrayList<Category>, val onCategoryIconClicked: (Category) -> Unit): RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {
    class CategoryViewHolder(val binding: ItemViewProductCategoryBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterCategory.CategoryViewHolder {
        val binding = ItemViewProductCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AdapterCategory.CategoryViewHolder,
        position: Int
    ) {
        val category = categoryList[position]
        holder.binding.apply {
            ivCategoryImage.setImageResource(category.image)
            tvCategoryTitle.text=category.title
        }
        holder.itemView.setOnClickListener {
            onCategoryIconClicked(category )
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}


