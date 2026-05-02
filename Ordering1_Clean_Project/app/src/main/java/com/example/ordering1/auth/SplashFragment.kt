package com.example.ordering1.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ordering1.R
import com.example.ordering1.activity.UsersMainActivity
import com.example.ordering1.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSplashBinding.inflate(inflater, container, false)

        Handler(Looper.getMainLooper()).postDelayed({

            if (auth.currentUser != null) {
                startActivity(Intent(requireActivity(), UsersMainActivity::class.java))
                requireActivity().finish()
            } else {
                findNavController().navigate(
                    R.id.action_splashFragment_to_signInFragment
                )
            }

        }, 2000)

        return binding.root
    }
}