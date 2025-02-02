package org.freedu.findyourfriend.view

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import org.freedu.findyourfriend.R
import org.freedu.findyourfriend.databinding.ActivityLoginBinding
import org.freedu.findyourfriend.viewmodel.AuthenticationViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        authenticationViewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "Please enter valid password", Toast.LENGTH_SHORT).show()
            } else {
                authenticationViewModel.login(email, password, {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                })
            }
        }

        binding.registerTxt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.forgotpass.setOnClickListener {
            showForgotPasswordDialog()
        }

    }

    private fun showForgotPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.fotgotpass, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.emailEt)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setView(dialogView)
            .setPositiveButton("Send Reset Email") { _, _ ->
                val email = emailEditText.text.toString().trim()
                if (email.isNotEmpty()) {
                    sendPasswordResetEmail(email)
                } else {
                    Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Reset email sent successfully", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage = it.exception?.message ?: "Error sending reset email"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
}