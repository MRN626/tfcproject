package com.esisalama.tfcproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.adapter.ProductCartAdapter
import com.esisalama.tfcproject.adapter.common.CustomClick
import com.esisalama.tfcproject.databinding.ActivityNfcCardInfoBinding
import com.esisalama.tfcproject.model.Operation
import com.esisalama.tfcproject.model.ProductCart
import com.google.gson.GsonBuilder
import org.jetbrains.anko.longToast

class NfcCardInfoActivity : AppCompatActivity(), CustomClick<ProductCart> {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityNfcCardInfoBinding>(this, R.layout.activity_nfc_card_info)
    }

    override fun onItemClick(item: ProductCart, view: View) {
        // This action is not allow here
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productCartAdapter = ProductCartAdapter(this)
        binding.adapter = productCartAdapter

        val gson = GsonBuilder().create()
        val intentExtra = intent.extras?.getString("nfc-info")

        try {
            val operation = gson.fromJson(intentExtra, Operation::class.java)
            productCartAdapter.submitList(operation.productCart)
            binding.totalPrice = operation.productCart.sumByDouble { it.product!!.price }
        } catch (e: Exception) {
            binding.totalPrice = 0.0
            longToast("Format des donnees non prise en charge")
        }
    }
}
