package com.esisalama.tfcproject.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

        binding.btnAddPayment.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddPaymentActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_about -> {
                true
            }

            R.id.menu_item_setting -> {
                true
            }

            R.id.menu_item_logout -> {
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
