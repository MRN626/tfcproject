package com.esisalama.tfcproject.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        formValidation()

        btnRegisterLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun formValidation() {
        val fullName = etFullName.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmation = etConfirmPassword.text.toString().trim()
        val phoneNumber = etUsername.text.toString().trim()

        when {
            fullName.isEmpty() -> {
                etFullName.error = getString(R.string.error_input)
                return
            }

            password.isEmpty() -> {
                etFullName.error = getString(R.string.error_input)
                return
            }

            confirmation.isEmpty() -> {
                etFullName.error = getString(R.string.error_input)
                return
            }

            phoneNumber.isEmpty() -> {
                etFullName.error = getString(R.string.error_input)
                return
            }

            else -> {
                registration(fullName, password, confirmation, phoneNumber)
            }
        }
    }

    private fun registration(fullName: String, password: String, confirmation: String, phoneNumber: String) {
        if (password != confirmation) {
            Toast.makeText(this, "Mot de passe ne correspond pas", Toast.LENGTH_LONG).show()
            return
        }

        val dialog = ProgressDialog(this).apply {
            setTitle("Connexion")
            setMessage("Connexion en cour patientez...")
        }

        val email = "$phoneNumber@test.com"

        dialog.show()
        val task = auth.createUserWithEmailAndPassword(email, password)
        task.addOnSuccessListener {
            val account = Account(uid = it.user!!.uid, ownerName = fullName)
            createAccount(account, dialog)
        }

        task.addOnFailureListener {
            dialog.hide()
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun createAccount(account: Account, dialog: ProgressDialog) {
        val accountRef = database.collection("accounts")
            .document(account.uid)
            .set(account)

        accountRef.addOnFailureListener {
            dialog.hide()
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }

        accountRef.addOnSuccessListener {
            dialog.hide()
            Toast.makeText(this, "Connexion reussi", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}