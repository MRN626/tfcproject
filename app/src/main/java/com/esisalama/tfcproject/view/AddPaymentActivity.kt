package com.esisalama.tfcproject.view

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.esisalama.tfcproject.BuildConfig
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.adapter.ProductCartAdapter
import com.esisalama.tfcproject.adapter.common.CustomClick
import com.esisalama.tfcproject.databinding.ActivityAddPaymentBinding
import com.esisalama.tfcproject.model.Operation
import com.esisalama.tfcproject.model.ProductCart
import com.esisalama.tfcproject.util.writeNfc
import com.esisalama.tfcproject.viewModel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import org.jetbrains.anko.longToast

class AddPaymentActivity : AppCompatActivity(), CustomClick<ProductCart> {

    private var stringJson: String? = null
    private var mNfcAdapter: NfcAdapter? = null
    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityAddPaymentBinding>(this, R.layout.activity_add_payment)
    }

    private val mViewModel by lazy {
        ViewModelProviders.of(this).get<ProductViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        val productCartAdapter = ProductCartAdapter(this)
        mViewModel.productCart.observe(this, Observer {
            Log.i(this@AddPaymentActivity::class.simpleName, it.toString())
            it?.let(productCartAdapter::submitList)
        })

        binding.viewModel = mViewModel
        binding.adapter = productCartAdapter
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

    fun finishAction(view: View) {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        mViewModel.productCart.observe(this, Observer {
            if (it.isNotEmpty()) {
                Log.e("finish", "isis")
                val accountNumber = FirebaseAuth.getInstance().currentUser?.uid ?: return@Observer
                val operation = Operation(accountNumber, it)
                stringJson = gson.toJson(operation)
                Toast.makeText(baseContext, "Rapprocher la puce pour enregistre les infos", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(baseContext, "Aucune informations", Toast.LENGTH_LONG).show()
            }
        })
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
        Log.e("maureen", "${intent.action}")
        val tagAction = intent.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == tagAction || NfcAdapter.ACTION_TAG_DISCOVERED == tagAction) {
            when (BuildConfig.FLAVOR) {
                "producer" -> processWriting(intent)
                else       -> Log.e("maureen", "else")
            }
        }

        super.onNewIntent(intent)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun processWriting(intent: Intent) {
        Log.e("maureen", "producer")
        if (stringJson == null) {
            longToast("Appuyer sur terminer l'enregistrement")
        } else {

            if (writeNfc(intent, stringJson!!)) {
                Toast.makeText(baseContext, "Saved", Toast.LENGTH_LONG).show()
                mViewModel.removeAll()
                stringJson = null

            } else {
                Log.e("maureen", "failed to write information")
            }
        }
    }

    fun addAction(view: View) {
        startActivity(Intent(this, ScanProductActivity::class.java))
    }

    override fun onItemClick(item: ProductCart, view: View) {
        val popupMenu = PopupMenu(this, view).apply {
            inflate(R.menu.product_cart_menu)
            setOnMenuItemClickListener {
                return@setOnMenuItemClickListener if (it.itemId == R.id.menu_item_product_cart) {
                    mViewModel.remove(item)
                    true
                } else {
                    false
                }
            }
        }

        popupMenu.show()
    }
}
