package com.esisalama.tfcproject.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esisalama.tfcproject.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLoginRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            checkingInput()
        }
    }

    private fun checkingInput() {
        val phoneNumber = etLoginUsername.text.toString()
        val password = etLoginPassword.text.toString()

        if (phoneNumber.isBlank()) {
            etLoginUsername.error = "Numero incorrect"
            return
        }

        if (password.isBlank() || password.length < 7) {
            etLoginPassword.error = "Mot de passe incorrect"
            return
        }

        login(phoneNumber, password)
    }

    private fun login(phoneNumber: String, password: String) {
        val dialog = ProgressDialog(this).apply {
            setTitle("Connexion")
            setMessage("Connexion en cour patientez...")
        }

        val authInstance = FirebaseAuth.getInstance()
        val email = "$phoneNumber@test.com"

        dialog.show()
        val loginTask = authInstance.signInWithEmailAndPassword(email, password)

        loginTask.addOnFailureListener {
            dialog.hide()
            Toast.makeText(this, "La connexion a echouée", Toast.LENGTH_LONG).show()
        }

        loginTask.addOnSuccessListener {
            dialog.hide()
            Toast.makeText(this, "Connexion reussi", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}