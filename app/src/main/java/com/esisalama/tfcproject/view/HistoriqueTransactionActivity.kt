package com.esisalama.tfcproject.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.adapter.HistoriqueOperationAdapter
import com.esisalama.tfcproject.adapter.common.CustomClick
import com.esisalama.tfcproject.databinding.ActivityHistoriqueTransactionBinding
import com.esisalama.tfcproject.model.HistoriqueOperation
import com.esisalama.tfcproject.util.LoadingState
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

        val progressDialog = ProgressDialog(this).apply {
            setTitle("Loading")
            setCancelable(true)
            setMessage("Recuperation de l'historique")
        }

        mViewModel.loadingState.observe(this, Observer {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    progressDialog.show()
                }

                LoadingState.Status.FAILED -> {
                    progressDialog.hide()
                    Toast.makeText(this, it.msg, Toast.LENGTH_LONG).show()
                }

                LoadingState.Status.SUCCESS -> {
                    progressDialog.hide()
                }
            }
        })

        binding.adapter = historiqueTransactionAdapter
    }
}
