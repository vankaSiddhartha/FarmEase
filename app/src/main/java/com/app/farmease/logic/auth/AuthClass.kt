package com.app.farmease.logic.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class AuthClass(private val auth: FirebaseAuth) {

    fun signUpOrSignInWithEmail(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,  // Callback for success
        onError: (String) -> Unit    // Callback for error
    ) {
        // Validate email and password
        val validationError = validateEmailAndPassword(email, password)
        if (validationError != null) {
            onError(validationError) // Return validation error
            return
        }

        // Try to create a new user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Successfully signed up
                    onSuccess("User signed up successfully!")
                } else {
                    // Check if the user already exists
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        // Try to sign in the existing user
                        signInWithEmail(email, password, onSuccess, onError)
                    } else {
                        // Handle other sign-up errors
                        onError(task.exception?.localizedMessage ?: "Sign-up failed.")
                    }
                }
            }
    }

    private fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Attempt to sign in
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Successfully signed in
                    onSuccess("User signed in successfully!")
                } else {
                    // Handle sign-in errors
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        onError("Invalid credentials. Please check your email and password.")
                    } else {
                        onError(task.exception?.localizedMessage ?: "Sign-in failed.")
                    }
                }
            }
    }

    private fun validateEmailAndPassword(email: String, password: String): String? {
        // Check if email is empty or invalid
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Please enter a valid email address."
        }

        // Check if password meets minimum requirements
        if (password.isBlank() || password.length < 6) {
            return "Password must be at least 6 characters long."
        }

        // Validation passed
        return null
    }
}
