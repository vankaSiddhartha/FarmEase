package com.app.farmease.onboarding

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.provider.Settings

import androidx.appcompat.app.AlertDialog
import com.app.farmease.MainActivity
import com.app.farmease.R
import com.app.farmease.databinding.ActivityOnBoardingBinding
import com.app.farmease.logic.auth.AuthClass
import com.google.firebase.auth.FirebaseAuth

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_location_permission, null)
        val dialogBuilder = AlertDialog.Builder(this)

            dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btn_deny).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_accept).setOnClickListener {
            // Handle location permission acceptance
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            alertDialog.dismiss()
        }
//        if(isLocationEnabled(this)) {
            alertDialog.show()
//        }else{
//            Toast.makeText(this, "noo", Toast.LENGTH_SHORT).show()
//
//        }

        binding.loadingContainer.visibility = View.GONE
        setContentView(binding.root)
        val auth = FirebaseAuth.getInstance()

        val authClass = AuthClass(auth)

        binding.proceedButton.setOnClickListener {
            binding.loadingContainer.visibility = View.VISIBLE

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            authClass.signUpOrSignInWithEmail(email,password, onSuccess ={
                Toast.makeText(this, "Welcome!!", Toast.LENGTH_SHORT).show()

                binding.loadingContainer.visibility = View.GONE
                startActivity(Intent(this,MainActivity::class.java))

            }, onError = {error->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                binding.loadingContainer.visibility = View.GONE

            })

        }
    }
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}