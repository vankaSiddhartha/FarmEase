package com.app.farmease.logic.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.farmease.R
import com.app.farmease.buy.BuyActivity
import com.app.farmease.databinding.FreshCardBinding
import com.app.farmease.databinding.SaleCardBinding
import com.app.farmease.logic.db.CartDatabaseHelper
import com.app.farmease.logic.model.Cart
import com.app.farmease.logic.model.Product
import com.bumptech.glide.Glide

class SaleProduct(private val productList: List<Product>,private val context: Context):RecyclerView.Adapter<SaleProduct.ViewHolder>(){
    private var filteredProductList = productList

    inner class ViewHolder(val binding:SaleCardBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product, position: Int) {
            // Set product name and price
            binding.productName.text = product.name
            binding.textView6.text = "₹${product.price}" // Bold price

            // Load product image using Glide
            Glide.with(binding.productImage.context)
                .load(product.image) // Replace `imageUrl` with the actual property
                .placeholder(R.drawable.home) // Optional placeholder
                .into(binding.productImage)
            binding.cart.setOnClickListener {
                val db = CartDatabaseHelper(context)
                val product = Cart(
                    product.name,
                    product.name,
                    product.description,
                    product.price,
                    product.quantity,
                    product.image,
                    1
                )
                if (db.addProduct(product)) {
                    Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                }
            }


            if (position % 2 == 0) {
                binding.root.background = ContextCompat.getDrawable(binding.root.context, R.drawable.round_two)

            } else {
                binding.root.background = ContextCompat.getDrawable(binding.root.context, R.drawable.rounded_corners)

            }
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

            // Apply rounded corners
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SaleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product,position)
    }
    fun filter(query: String) {
        filteredProductList = if (query.isEmpty()) {
            productList
        } else {
            productList.filter { it.name?.contains(query, ignoreCase = true) ?: false }
        }
        notifyDataSetChanged()
    }
}