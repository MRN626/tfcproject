package com.esisalama.tfcproject.view

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.esisalama.tfcproject.BuildConfig
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.databinding.ActivityMainBinding
import com.esisalama.tfcproject.model.Account
import com.esisalama.tfcproject.util.readFromNfc
import com.esisalama.tfcproject.util.writeNfc
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private var mNfcAdapter: NfcAdapter? = null
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

        setupNfcAdapter()
    }

    private fun setupNfcAdapter() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val isAvailable = mNfcAdapter != null && mNfcAdapter!!.isEnabled
        if (isAvailable) {
            Toast.makeText(this, "NFC disponible", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "NFC non-disponible", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()

        mNfcAdapter ?: return

        // Dispatch les intents à l'activity
        val intent = Intent(this, this::class.java).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val intentFilter = arrayOf<IntentFilter>()
        mNfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilter, null)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNewIntent(intent: Intent) {
        Log.e("ericampire", "${intent.action}")
        val tagAction = intent.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == tagAction || NfcAdapter.ACTION_TAG_DISCOVERED == tagAction) {
            when (BuildConfig.FLAVOR) {
                "customer" -> {
                    val result = readFromNfc(intent) ?: return
                    showTagInfo(result)
                }

                else -> {
                    Log.e("ericampire", "else")
                }
            }
        }

        super.onNewIntent(intent)
    }

    private fun showTagInfo(result: String) {
        val nfcCartIntent = Intent(baseContext, NfcCardInfoActivity::class.java).apply {
            putExtra("nfc-info", result)
        }

        startActivity(nfcCartIntent)
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
