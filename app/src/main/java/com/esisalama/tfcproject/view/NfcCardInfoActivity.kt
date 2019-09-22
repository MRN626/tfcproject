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

class NfcCardInfoActivity : AppCompatActivity(), CustomClick<ProductCart> {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityNfcCardInfoBinding>(this, R.layout.activity_nfc_card_info)
    }

    override fun onItemClick(item: ProductCart, view: View) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gson = GsonBuilder().create()
        val intentExtra = intent.extras?.getString("nfc-info")
        val operation = gson.fromJson(intentExtra, Operation::class.java)
        val productCartAdapter = ProductCartAdapter(this)

        productCartAdapter.submitList(operation.productCart)
        binding.run {
            adapter = productCartAdapter
            totalPrice = operation.productCart.sumByDouble { it.product!!.price }
        }
    }
}
