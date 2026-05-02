package com.example.ordering1.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ordering1.activity.AuthMainActivity
import com.example.ordering1.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()

        setupUserInfo()
        setupClicks()
    }

    private fun setupUserInfo() {

        val user = auth.currentUser

        if (user != null) {
            binding.tvPhoneNumber.text = user.phoneNumber ?: "No number"
            binding.tvUserName.text = "My Account"
        } else {
            binding.tvPhoneNumber.text = "Not Logged In"
        }
    }

    private fun setupClicks() {

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.layoutOrders.setOnClickListener {
            Toast.makeText(requireContext(), "Orders coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.layoutAddress.setOnClickListener {
            Toast.makeText(requireContext(), "Address coming soon", Toast.LENGTH_SHORT).show()
        }

        // ✅ FIXED LOGOUT (NO loginFragment ERROR)
        binding.layoutLogout.setOnClickListener {

            auth.signOut()

            val intent = Intent(requireActivity(), AuthMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}