package com.esisalama.tfcproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.databinding.ActivityMainBinding
import com.esisalama.tfcproject.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val database by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUserUid = auth.currentUser?.uid ?: return
        val accountRef = database.collection("accounts").document(currentUserUid)

        accountRef.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                return@addSnapshotListener
            }

            binding.account = snapshot.toObject(Account::class.java)
        }
    }
}
