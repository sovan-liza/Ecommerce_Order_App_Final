package com.example.adminordering.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminordering.databinding.ItemViewProductCategoriesBinding
import com.example.adminordering.model.Categories

class CategoriesAdapter(
    private val categoriesArrayList: ArrayList<Categories>,
    val onCategoryClicked: (Categories) -> Unit,
) : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    class CategoriesViewHolder(
        val binding: ItemViewProductCategoriesBinding
    ) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriesViewHolder {
        return CategoriesViewHolder(ItemViewProductCategoriesBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun getItemCount(): Int {
        return categoriesArrayList.size
    }
    override fun onBindViewHolder(
        holder: CategoriesViewHolder,
        position: Int
    ) {
        val category=categoriesArrayList[position]
        holder.binding.apply {
            ivCategoryImage.setImageResource(category.icon)
            tvCategory.text=category.category
        }
        holder.itemView.setOnClickListener {
            onCategoryClicked(category)
        }
    }





}