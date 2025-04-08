package com.app.farmease.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.farmease.R
import com.app.farmease.databinding.FragmentHomeBinding
import com.app.farmease.logic.server.GetData

class Home : Fragment() {
    private lateinit var binding:FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val getData = GetData(requireContext(), binding.horizontalRecyclerView,binding.pwimg)
        getData.retrieveProductsFromDatabase()
        val getSale = GetData(requireContext(),binding.fresh,binding.pwimg,1)
        getSale.retrieveProductsFromDatabase()
        binding.categoryChipGroup.setOnClickListener{
            val getData = GetData(requireContext(), binding.horizontalRecyclerView,binding.pwimg)
            getData.retrieveProductsFromDatabase()
            val getSale = GetData(requireContext(),binding.fresh,binding.pwimg,1)
            getSale.retrieveProductsFromDatabase()

        }
        // Inside your Fragment or Activity




        return binding.root
    }


}