package com.app.farmease.startupScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.app.farmease.R
import com.app.farmease.onboarding.OnBoardingActivity
import com.google.firebase.auth.FirebaseAuth

class StartUpScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up_screen)
        if(FirebaseAuth.getInstance().currentUser?.email != null){
            Handler(mainLooper).postDelayed({
                startActivity(Intent(this,OnBoardingActivity::class.java))
            },1000)
        }
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this,OnBoardingActivity::class.java))
        },1000)
    }
}