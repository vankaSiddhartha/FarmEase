package com.app.farmease.logic.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.farmease.R
import com.app.farmease.buy.BuyActivity
import com.app.farmease.databinding.FreshCardBinding
import com.app.farmease.logic.model.Product
import com.bumptech.glide.Glide

class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    // ViewHolder class using ViewBinding
    inner class ViewHolder(val binding: FreshCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.textView6.text = "₹${product.price}" // Bold price
//            binding.freeShippingBadge.visibility =
//                if (product.isFreeShipping) View.VISIBLE else View.GONE

            // Load product image using Glide or any other image loader
            Glide.with(binding.productImage.context)
                .load(product.image) // Replace `imageUrl` with the actual property
                .placeholder(R.drawable.home) // Optional placeholder
                .into(binding.productImage)
            binding.root.setOnClickListener {
                // Create an Intent to pass product data to BuyActivity
                val intent = Intent(binding.root.context, BuyActivity::class.java).apply {
                    // Put all the necessary product details in the Intent
                    putExtra("PRODUCT_NAME", product.name)
                    putExtra("PRODUCT_PRICE", product.price)
                    putExtra("PRODUCT_IMAGE", product.image)
                    putExtra("PRODUCT_DIS",product.description)
                    putExtra("ID",product.id)
                    putExtra("Q",product.quantity)

                    // Add more product details if necessary
                }
                // Start BuyActivity
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FreshCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size
}
