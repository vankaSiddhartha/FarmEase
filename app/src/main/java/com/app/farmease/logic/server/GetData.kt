package com.app.farmease.logic.server

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.farmease.R
import com.app.farmease.logic.adapter.ProductAdapter
import com.app.farmease.logic.adapter.SaleProduct
import com.app.farmease.logic.model.Product
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GetData(private val context: Context, private val recyclerView: RecyclerView, private val loading: ImageView,private val type: Int = 0) {

    private var productList: MutableList<Product> = mutableListOf()

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun retrieveProductsFromDatabase() {
        Glide.with(context).load(R.drawable.load).into(loading)
        val databaseReference = FirebaseDatabase.getInstance().getReference("products")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()  // Clear existing list before adding new data
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        productList.add(it)
                    }
                }
                // Pass the productList to updateUI
                if (type == 0) {
                    updateUI(productList.shuffled())
                } else {
                    updateUIFresh(productList.shuffled())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to retrieve data: ${error.message}")
            }
        })
    }

    private fun updateUI(productList: List<Product>) {
        if (productList.isEmpty()) {
            showToast("No products found.")
            loading.visibility = View.GONE
        } else {
            val productAdapter = ProductAdapter(productList)
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = productAdapter
            productAdapter.notifyDataSetChanged()
            loading.visibility = View.GONE

        }
    }

    private fun updateUIFresh(productList: List<Product>) {
        if (productList.isEmpty()) {
            showToast("No products found.")
            loading.visibility = View.GONE
        } else {
            val productAdapter = SaleProduct(productList,context)
            val layoutManager = GridLayoutManager(context, 2)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = productAdapter
            productAdapter.notifyDataSetChanged()
            loading.visibility = View.GONE
        }
    }

    // Method to filter products based on the search query
    fun queryMethod(text: String) {
        val filteredList = productList.filter {
            it.name?.contains(text, ignoreCase = true) ?: false // Assuming `name` is a field in the Product class
        }
        // After filtering, update the UI with the filtered list
        if (type == 0) {
            updateUI(filteredList)
        } else {
            updateUIFresh(filteredList)
        }
    }
}
