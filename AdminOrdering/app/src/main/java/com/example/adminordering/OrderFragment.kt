package com.example.adminordering

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase

class OrderFragment : Fragment() {

    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 👉 Get orderId from arguments (IMPORTANT FIX)
        orderId = arguments?.getString("orderId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_order, container, false)

        setStatusBarColor()

        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        val btnReject = view.findViewById<Button>(R.id.btnReject)

        btnConfirm.setOnClickListener {
            updateStatus("Confirmed")
        }

        btnReject.setOnClickListener {
            updateStatus("Rejected")
        }

        return view
    }

    // ================= STATUS BAR FIX =================
    private fun setStatusBarColor() {

        requireActivity().window.apply {

            statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.dar_yellow)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    // ================= UPDATE ORDER STATUS =================
    private fun updateStatus(status: String) {

        val id = orderId

        if (id.isNullOrEmpty()) {
            return
        }

        FirebaseDatabase.getInstance()
            .getReference("Orders")
            .child(id)
            .child("status")
            .setValue(status)
    }

    companion object {

        // 👉 Proper way to pass orderId
        fun newInstance(orderId: String): OrderFragment {
            val fragment = OrderFragment()
            val args = Bundle()
            args.putString("orderId", orderId)
            fragment.arguments = args
            return fragment
        }
    }
}