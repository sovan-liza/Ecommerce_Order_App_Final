package com.example.adminordering.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminordering.AdminMainActivity
import com.example.adminordering.R
import com.example.adminordering.Users
import com.example.adminordering.Utils
import com.example.adminordering.databinding.FragmentOTPBinding
import com.example.adminordering.model.Admins
import com.example.adminordering.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.text.clear


class OTPFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentOTPBinding
    private lateinit var userNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(layoutInflater)
        getUserNumber()
        customizingEnteringOTP()
        sendOTP()
        onLoginButtonClicked()
        onBackButtonClicked()
        return binding.root
    }
    private fun onBackButtonClicked() {
        binding.tbOtpFragment.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun onLoginButtonClicked() {

        binding.btnLogin.setOnClickListener {

            Utils.showDialog(requireContext(), "Signing you in...")

            val editTexts = arrayOf(
                binding.etOtp1,
                binding.etOtp2,
                binding.etOtp3,
                binding.etOtp4,
                binding.etOtp5,
                binding.etOtp6
            )

            val otp = editTexts.joinToString("") { it.text.toString() }

            if (otp.length < editTexts.size) {
                Utils.hideDialog()
                Utils.showToast(requireContext(), "Please enter correct OTP")
            } else {
                editTexts.forEach { it.text?.clear();it.clearFocus() }
                verifyOtp(otp)
            }
        }
    }
    private fun verifyOtp(otp: String) {

        val admins =
            Admins(uid = null, adminPhoneNumber = userNumber)
        viewModel.signInWithPhoneAuthCredential(otp, userNumber,admins)
        lifecycleScope.launch {
            viewModel.isSingnedInSuccssfully.collect {


                Utils.hideDialog()
                if (it) {

                    Utils.showToast(requireContext(), "Logged in successfully")
                    startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }
    private fun sendOTP() {

        Utils.showDialog(requireContext(), "Sending OTP...")

        viewModel.sendOTP(userNumber, requireActivity())

        lifecycleScope.launch {
            viewModel.otpSent.collect { isSent ->

                Utils.hideDialog()

                if (isSent) {
                    Utils.showToast(requireContext(), "OTP sent successfully")
                } else {
                    Utils.showToast(requireContext(), "Failed to send OTP")
                }


            }
        }
    }
    private fun customizingEnteringOTP() {

        val editTexts = arrayOf(
            binding.etOtp1,
            binding.etOtp2,
            binding.etOtp3,
            binding.etOtp4,
            binding.etOtp5,
            binding.etOtp6
        )

        for (i in editTexts.indices) {

            editTexts[i].addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {

                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    } else if (s?.isEmpty() == true && i > 0) {
                        editTexts[i - 1].requestFocus()
                    }
                }
            })
        }
    }
    private fun getUserNumber() {

        userNumber = arguments?.getString("number") ?: ""

        binding.tvUserNumber.text = userNumber

        if (userNumber.isEmpty()) {
            Utils.showToast(requireContext(), "Invalid phone number")
            findNavController().popBackStack()
        }
    }
}