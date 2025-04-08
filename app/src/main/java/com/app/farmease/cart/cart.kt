package com.app.farmease.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.farmease.R
import com.app.farmease.databinding.FragmentCartBinding
import com.app.farmease.logic.adapter.CartAdapter
import com.app.farmease.logic.db.CartDatabaseHelper
import com.app.farmease.logic.model.Cart
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject


class cart : Fragment(),PaymentResultListener {
    private lateinit var binding:FragmentCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val db = CartDatabaseHelper(requireContext())
        val adapter =  CartAdapter(requireContext(), db )
        binding.cartRecyclerView.adapter = adapter
        adapter.updateItems(db.getAllProducts())
        if (db.getAllProducts().isEmpty()) {
            binding.bottomPanel.visibility = View.GONE
        } else {

            binding.emptyText.visibility = View.GONE

            binding.bottomPanel.visibility = View.VISIBLE

        }
        binding.checkoutButton.setOnClickListener {
            var total = 0
            for (i in db.getAllProducts()) {
                val price = i.price?.toIntOrNull() ?: 0 // Safely convert price to an integer
                total += (i.count * price) // Perform the multiplication and add to total
            }
            total *= 100
            startPayment(total.toString(),FirebaseAuth.getInstance().currentUser?.email.toString())
        }
        return binding.root
    }
    private fun startPayment(price:String,email:String) {
        val checkout = Checkout()

        // Set your Razorpay API Key
        checkout.setKeyID("YOUR_API_KEY")

        try {
            val options = JSONObject()
            options.put("name", "FarmEase") // Name of your app or business
            options.put("description", "Payment for Order") // Payment description
// Optional
            options.put("currency", "INR") // Currency code
            options.put("amount", price) // Amount in paise (50000 = ₹500)

            val prefill = JSONObject()
            prefill.put("email", "test@example.com") // User's email
            prefill.put("contact", "9876543210") // User's phone number

            options.put("prefill", prefill)

            checkout.open(requireActivity(), options)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(p0: String?) {

    }

    override fun onPaymentError(p0: Int, p1: String?) {

    }

}