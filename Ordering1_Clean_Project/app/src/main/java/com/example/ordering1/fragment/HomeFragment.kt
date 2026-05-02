package com.example.ordering1.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ordering1.Constatns
import com.example.ordering1.R
import com.example.ordering1.adapters.AdapterCategory
import com.example.ordering1.databinding.FragmentHomeBinding
import com.example.ordering1.model.Category
import androidx.fragment.app.viewModels
import android.util.Log
import com.example.ordering1.viewmodels.UserViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setStatusBarColor()
        setAllCategories()
        navigatingToSearchFragment()
        navigateToProfile()   // ✅ FIXED
        observeData()

        return binding.root
    }

    private fun navigateToProfile() {
        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profile2)
        }
    }

    private fun observeData() {
        viewModel.getAll().observe(viewLifecycleOwner) {
            for (i in it) {
                Log.d("DATA", i.productTitle.toString())
            }
        }
    }

    private fun navigatingToSearchFragment() {
        binding.searchCv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()

        for (i in 0 until Constatns.allProductsCategoryIcon.size) {
            categoryList.add(
                Category(
                    Constatns.allProductsCatagory[i],
                    Constatns.allProductsCategoryIcon[i]
                )
            )
        }

        binding.rvCategories.adapter =
            AdapterCategory(categoryList, ::onCategoryClick)
    }

    private fun onCategoryClick(category: Category) {
        val bundle = Bundle()
        bundle.putString("category", category.title)

        findNavController().navigate(
            R.id.action_homeFragment_to_categoryFragment,
            bundle
        )
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.dar_yellow)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}