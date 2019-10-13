package com.esisalama.tfcproject.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.adapter.HistoriqueOperationAdapter
import com.esisalama.tfcproject.adapter.common.CustomClick
import com.esisalama.tfcproject.databinding.ActivityHistoriqueTransactionBinding
import com.esisalama.tfcproject.model.HistoriqueOperation
import com.esisalama.tfcproject.viewModel.HistoriqueTransactionViewModel

class HistoriqueTransactionActivity : AppCompatActivity() {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityHistoriqueTransactionBinding>(this, R.layout.activity_historique_transaction)
    }

    private val mViewModel by lazy {
        ViewModelProviders.of(this).get(HistoriqueTransactionViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val historiqueTransactionAdapter = HistoriqueOperationAdapter(object : CustomClick<HistoriqueOperation> {
            override fun onItemClick(item: HistoriqueOperation, view: View) {
                // Show detail transaction
                val detailIntent = Intent(this@HistoriqueTransactionActivity, DetailHistoriqueActivity::class.java)
                detailIntent.putExtra("operation", item)
                startActivity(detailIntent)
            }
        })

        mViewModel.historique.observe(this, Observer {
            historiqueTransactionAdapter.submitList(it)
        })

        binding.adapter = historiqueTransactionAdapter
    }
}
