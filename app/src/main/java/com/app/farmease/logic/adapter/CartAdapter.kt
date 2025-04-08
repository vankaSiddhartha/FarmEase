package com.app.farmease.logic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.farmease.R
import com.app.farmease.logic.db.CartDatabaseHelper
import com.app.farmease.logic.model.Cart
import com.bumptech.glide.Glide

class CartAdapter(
    private val context: Context,
    private val dbHelper: CartDatabaseHelper
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartItems = mutableListOf<Cart>()
    private var onItemChangedListener: (() -> Unit)? = null

    fun setOnItemChangedListener(listener: () -> Unit) {
        onItemChangedListener = listener
    }

    fun updateItems(newItems: List<Cart>) {
        cartItems.clear()
        cartItems.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.imageView5)
        private val productName: TextView = itemView.findViewById(R.id.textView10)
        private val pricePerUnit: TextView = itemView.findViewById(R.id.textView17)
        private val totalPrice: TextView = itemView.findViewById(R.id.priceText)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        private val decrementButton: ImageButton = itemView.findViewById(R.id.decrementButton)
        private val incrementButton: ImageButton = itemView.findViewById(R.id.incrementButton)
        private val quantityText: TextView = itemView.findViewById(R.id.quantityText)

        fun bind(item: Cart) {
            productName.text = item.name
            pricePerUnit.text = "₹"+item.price+"/kg"
            quantityText.text = item.count.toString()

            // Calculate total price
            val unitPrice = item.price?.replace("₹", "")?.toDoubleOrNull() ?: 0.0
            val total = unitPrice * item.count
            totalPrice.text = String.format("₹%.2f", total)

            // Load image using your preferred image loading library
            // For example, using Glide:
            Glide.with(context)
                .load(item.image)
                .placeholder(R.drawable.home)
                .error(R.drawable.home)
                .into(productImage)

            // Handle delete button click
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    removeItem(position)
                }
            }

            // Handle increment button click
            incrementButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    updateItemCount(position, item.count + 1)
                }
            }

            // Handle decrement button click
            decrementButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && item.count > 1) {
                    updateItemCount(position, item.count - 1)
                }
            }

            // Update decrement button state
            decrementButton.isEnabled = item.count > 1
            decrementButton.alpha = if (item.count > 1) 1.0f else 0.5f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    private fun removeItem(position: Int) {
        val item = cartItems[position]
        item.id?.let { dbHelper.removeProduct(it) }
        cartItems.removeAt(position)
        notifyItemRemoved(position)
        onItemChangedListener?.invoke()
    }

    private fun updateItemCount(position: Int, newCount: Int) {
        val item = cartItems[position]
        if (item.id?.let { dbHelper.updateProductCount(it, newCount) } == true) {
            cartItems[position] = item.copy(count = newCount)
            notifyItemChanged(position)
            onItemChangedListener?.invoke()
        }
    }


}