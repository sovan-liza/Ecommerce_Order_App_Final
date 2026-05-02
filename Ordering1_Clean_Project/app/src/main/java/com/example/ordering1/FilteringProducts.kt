package com.example.ordering1


import android.widget.Filter
import com.example.ordering1.adapters.AdapterProduct
import com.example.ordering1.model.Product
import java.util.Locale
import kotlin.collections.any
import kotlin.text.contains
import kotlin.text.isNullOrEmpty
import kotlin.text.split
import kotlin.text.trim
import kotlin.text.uppercase


class FilteringProducts(
    val adapter : AdapterProduct,

    val filter : ArrayList<Product>
) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults? {
        val result = FilterResults()
        if (!constraint.isNullOrEmpty()){
            val filteredList = kotlin.collections.ArrayList<Product>()
            val query = constraint.toString().trim().uppercase(Locale.getDefault()).split(" ")
            for (products in filter){
                if (query.any {

                        products.productTitle?.uppercase(Locale.getDefault())?.contains(it ) == true ||
                                products.productCategory?.uppercase(Locale.getDefault())?.contains(it ) == true ||
                                products.productPrice?.toString()?.uppercase(Locale.getDefault())?.contains(it ) == true ||
                                products.productType?.uppercase(Locale.getDefault())?.contains(it ) == true
                    }){
                    filteredList.add(products)
                }
            }
            result.values = filteredList
            result.count = filteredList.size
        }
        else{
            result.values = filter
            result.count = filter.size
        }

        return result
    }

    override fun publishResults(
        constraint: CharSequence?,
        results: FilterResults?
    ) {
        val filteredList = results?.values as? ArrayList<Product> ?: kotlin.collections.ArrayList()
        adapter.differ.submitList(filteredList)
    }

}