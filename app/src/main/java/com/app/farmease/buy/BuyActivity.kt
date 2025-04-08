package com.app.farmease.buy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.app.farmease.R
import com.app.farmease.databinding.ActivityBuyBinding
import com.app.farmease.logic.db.CartDatabaseHelper
import com.app.farmease.logic.model.Cart
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.util.UUID

class BuyActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityBuyBinding
    private var counterValue = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )


        // Retrieve the data passed from the ProductAdapter
        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productPrice = intent.getStringExtra("PRODUCT_PRICE")
        val productImage = intent.getStringExtra("PRODUCT_IMAGE")
        val productDis = intent.getStringExtra("PRODUCT_DIS")
        val id = intent.getStringExtra("ID")
        val quanity =intent.getStringExtra("Q")

        // Set the received data to the respective UI elements
        binding.textView12.text = productName
        binding.textView14.text = "₹$productPrice"
        binding.textView13.text = productDis
        Glide.with(this)
            .load(productImage)
            .into(binding.imageView3)

        // Set up other UI elements (e.g., Buy button, etc.)
        binding.button.setOnClickListener {
            // Handle Buy button click, if necessary
        }
        // Set the initial counter value
        binding.value.text = counterValue.toString()

        // Decrease button logic
        binding.textView15.setOnClickListener {
            if (counterValue > 0) {
                counterValue--
                binding.value.text = counterValue.toString()
            }
        }

        // Increase button logic
        binding.textView16.setOnClickListener {
            counterValue++
            binding.value.text = counterValue.toString()
        }
        binding.back.setOnClickListener {
            finish()
        }
        binding.share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain" // Specify the type of content
                putExtra(Intent.EXTRA_TEXT, "This is the content I want to share on WhatsApp!") // Replace with your content
            }
            try {
                // Specify WhatsApp package to share explicitly
                shareIntent.setPackage("com.whatsapp")
                startActivity(shareIntent)
            } catch (e: Exception) {
                // Handle the case when WhatsApp is not installed
                Toast.makeText(this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show()
            }
        }

        //cart
        val db = CartDatabaseHelper(this)
        binding.constraintLayout2.setOnClickListener {
        val product = Cart(productName,productName,productDis,productPrice,quanity,productImage,counterValue)
            if (db.addProduct(product)) {
                Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
        }
        binding.button.setOnClickListener {
            val p = productPrice?.toIntOrNull() ?: 0  * 100
            startPayment(p.toString(),FirebaseAuth.getInstance().currentUser?.email.toString())
        }
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

            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Payment Successful: $p0", Toast.LENGTH_LONG).show()

    }

    override fun onPaymentError(p0: Int, p1: String?) {

        Toast.makeText(this, "Payment Failed: $p0", Toast.LENGTH_LONG).show()

    }

}

