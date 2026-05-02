package com.example.ordering1.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.ordering1.Utils
import com.example.ordering1.activity.UsersMainActivity
import com.example.ordering1.databinding.FragmentOTPBinding
import com.example.ordering1.model.Users
import com.example.ordering1.repository.UserCreateHelper
import com.example.ordering1.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentOTPBinding

    private lateinit var userNumber: String
    private val userCreateHelper = UserCreateHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOTPBinding.inflate(inflater, container, false)

        getUserNumber()
        customizingEnteringOTP()
        sendOTP()
        onLoginButtonClicked()
        onBackButtonClicked()

        observeViewModel()

        return binding.root
    }

    // =========================
    // OBSERVE VIEWMODEL SAFELY
    // =========================
    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.otpSent.collect { isSent ->
                        Utils.hideDialog()
                        if (isSent) {
                            Utils.showToast(requireContext(), "OTP sent successfully")
                        } else {
                            Utils.showToast(requireContext(), "Failed to send OTP")
                        }
                    }
                }

                launch {
                    viewModel.isSignedInSuccessfully.collect { isSuccess ->

                        if (isSuccess) {

                            val uid = Utils.getCurrentUserId()

                            if (!uid.isNullOrEmpty()) {
                                userCreateHelper.createIfNotExists(uid, userNumber)
                            }

                            Utils.hideDialog()
                            Utils.showToast(requireContext(), "Login successful")

                            startActivity(
                                Intent(requireActivity(), UsersMainActivity::class.java)
                            )

                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }

    // =========================
    // PHONE NUMBER
    // =========================
    private fun getUserNumber() {

        userNumber = arguments?.getString("number") ?: ""

        binding.tvUserNumber.text = userNumber

        if (userNumber.isEmpty()) {
            Utils.showToast(requireContext(), "Invalid phone number")
            findNavController().popBackStack()
        }
    }

    // =========================
    // SEND OTP
    // =========================
    private fun sendOTP() {

        Utils.showDialog(requireContext(), "Sending OTP...")

        viewModel.sendOTP(userNumber, requireActivity())
    }

    // =========================
    // VERIFY OTP
    // =========================
    private fun onLoginButtonClicked() {

        binding.btnLogin.setOnClickListener {

            val editTexts = arrayOf(
                binding.etOtp1, binding.etOtp2, binding.etOtp3,
                binding.etOtp4, binding.etOtp5, binding.etOtp6
            )

            val otp = editTexts.joinToString("") { it.text.toString() }

            if (otp.length < 6) {
                Utils.showToast(requireContext(), "Enter valid OTP")
                return@setOnClickListener
            }

            Utils.showDialog(requireContext(), "Verifying...")

            val user = Users(
                uid = Utils.getCurrentUserId(),
                userPhoneNumber = userNumber,
                userAddress = ""
            )

            viewModel.signInWithPhoneAuthCredential(otp, userNumber, user)

            editTexts.forEach {
                it.text?.clear()
                it.clearFocus()
            }
        }
    }

    // =========================
    // BACK BUTTON
    // =========================
    private fun onBackButtonClicked() {
        binding.tbOtpFragment.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    // =========================
    // OTP INPUT AUTO MOVE
    // =========================
    private fun customizingEnteringOTP() {

        val editTexts = arrayOf(
            binding.etOtp1, binding.etOtp2, binding.etOtp3,
            binding.etOtp4, binding.etOtp5, binding.etOtp6
        )

        for (i in editTexts.indices) {

            editTexts[i].addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    } else if (s?.isEmpty() == true && i > 0) {
                        editTexts[i - 1].requestFocus()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }
}