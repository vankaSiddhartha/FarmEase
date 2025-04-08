package com.app.farmease.settings

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.farmease.R
import com.app.farmease.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth




class Settings : Fragment() {
private lateinit var binding:FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        binding.google.setOnClickListener {
            openUrl(requireContext(),"https://play.google.com/store/apps/details?id=com.google.android.apps.nbu.paisa.user")
        }
        binding.phonepay.setOnClickListener {
            openPhonePe()
        }
        binding.changePassword.setOnClickListener {
            resetPassword(FirebaseAuth.getInstance().currentUser?.email.toString()) { success, message ->
                if (success) {
                    showToast("Password reset email sent!")
                } else {
                    showToast("Failed to send reset email: $message")
                }
            }

        }
        binding.prv.setOnClickListener {
            openUrl(requireContext(),"https://www.termsfeed.com/live/a1af5fcb-7a2f-47ff-9500-7af2fe3b2a30")
        }
        binding.tnc.setOnClickListener {
            openUrl(requireContext(),"https://www.termsfeed.com/live/a1af5fcb-7a2f-47ff-9500-7af2fe3b2a30")

        }

        return binding.root
    }
    fun openGooglePay() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage("com.google.android.apps.nbu.paisa.user") // Google Pay package name
        }

        try {
            startActivity(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            // Handle the case where Google Pay is not installed
            // Optionally, redirect to the Play Store to install Google Pay
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.nbu.paisa.user")
            }
            startActivity(playStoreIntent)
        }
    }
    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null) // Email sent
                } else {
                    onComplete(false, task.exception?.message) // Error message
                }
            }
    }
    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Optional: Show a Toast or Log if the URL fails to open
        }
    }
    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), message, duration).show()
    }
    fun openPhonePe() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage("com.phonepe.app") // PhonePe package name
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the case where PhonePe is not installed
            // Redirect to the Play Store to install PhonePe
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.phonepe.app")
            }
            startActivity(playStoreIntent)
        }
    }

}