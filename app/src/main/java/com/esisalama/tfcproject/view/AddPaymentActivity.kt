package com.esisalama.tfcproject.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.adapter.ProductCartAdapter
import com.esisalama.tfcproject.adapter.common.CustomClick
import com.esisalama.tfcproject.databinding.ActivityAddPaymentBinding
import com.esisalama.tfcproject.model.ProductCart
import com.esisalama.tfcproject.viewModel.ProductViewModel

class AddPaymentActivity : AppCompatActivity(), CustomClick<ProductCart> {

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
    }

    fun finishAction(view: View) {
        // Todo: Save data in nfc tag
        // Todo: remove data
        // Todo: Show loading
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
