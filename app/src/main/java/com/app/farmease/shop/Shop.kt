package com.app.farmease.shop

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.farmease.R
import com.app.farmease.databinding.FragmentShopBinding
import com.app.farmease.logic.server.GetData


class Shop : Fragment() {
private lateinit var binding:FragmentShopBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        val getData = GetData(requireContext(),binding.shopRv,binding.pwimg,1)
        getData.retrieveProductsFromDatabase()

        binding.roundedEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this case, so leave it empty
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // You can access the text here if needed
            }

            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString()  // Get the query from the EditText
                getData.queryMethod(query)  // Filter the products based on query
            }
        })
        return binding.root
    }


}