package com.example.ordering1.auth

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.ordering1.R
import com.example.ordering1.Utils
import com.example.ordering1.databinding.FragmentSingInBinding


class SingInFragment : Fragment() {


    private lateinit var binding: FragmentSingInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSingInBinding.inflate(layoutInflater)
        setStatusBarColor()
        getUserNumber()
        onContinueButtonClick()

        return binding.root
    }

    private fun onContinueButtonClick() {
        binding.btnContinue.setOnClickListener {

            var number = binding.etUserNumber.text.toString().trim()

            if (number.isEmpty() || (number.length != 9 && number.length != 10)) {
                Utils.showToast(requireContext(), "Please enter a valid phone number")
                return@setOnClickListener
            }

            // ✅ Remove leading 0 if exists
            if (number.startsWith("0")) {
                number = number.substring(1)
            }

            // ✅ Add Cambodia country code
            val fullNumber = "+855$number"

            val bundle = Bundle().apply {
                putString("number", fullNumber)
            }

            findNavController().navigate(
                R.id.action_signInFragment_to_OTPFragment,
                bundle
            )
        }
    }


    private fun getUserNumber() {
       binding.etUserNumber.addTextChangedListener( object : TextWatcher{
           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

           }

           override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
               val len = number?.length
               if(len == 9 || len == 10){
                   binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(),
                       R.color.green))
               }
               else{
                   binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(),
                       R.color.yellow))
               }
           }

           override fun afterTextChanged(s: Editable?) {

           }
       } )
    }


    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors= ContextCompat.getColor(requireContext(), R.color.dar_yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}