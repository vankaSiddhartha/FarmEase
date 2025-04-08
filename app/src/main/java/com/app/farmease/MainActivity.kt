package com.app.farmease

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.farmease.cart.cart
import com.app.farmease.databinding.ActivityMainBinding
import com.app.farmease.home.Home
import com.app.farmease.settings.Settings
import com.app.farmease.shop.Shop

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
        if (savedInstanceState == null) {
            navigateToFragment(
                fragmentManager = supportFragmentManager,
                containerId = R.id.container_box,
                fragment = Home(),
                addToBackStack = true,



                )
        }
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
              R.id.home->{
                  navigateToFragment(
                     fragmentManager = supportFragmentManager,
                      containerId = R.id.container_box,
                      fragment = Home(),
                      addToBackStack = true,



                  )
                  true

              }
            R.id.bag->{
                navigateToFragment(
                    fragmentManager = supportFragmentManager,
                    containerId = R.id.container_box,
                    fragment = Shop(),
                    addToBackStack = true,



                    )
                true
            }
                R.id.cart->{
                    navigateToFragment(
                        fragmentManager = supportFragmentManager,
                        containerId = R.id.container_box,
                        fragment = cart(),
                        addToBackStack = true,



                        )
                    true
                }
            R.id.profile->{
                navigateToFragment(
                    fragmentManager = supportFragmentManager,
                    containerId = R.id.container_box,
                    fragment = Settings(),
                    addToBackStack = true,



                    )
                true
            }
            else->true
            }
        }
    }
    fun navigateToFragment(
        fragmentManager: FragmentManager,
        containerId: Int,
        fragment: Fragment,
        addToBackStack: Boolean = true
    ) {
        val transaction = fragmentManager.beginTransaction()
            .replace(containerId, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

}