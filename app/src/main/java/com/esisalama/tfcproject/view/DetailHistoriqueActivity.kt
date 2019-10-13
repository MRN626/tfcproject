package com.esisalama.tfcproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.databinding.ActivityDetailHistoriqueBinding
import com.esisalama.tfcproject.databinding.ActivityHistoriqueTransactionBinding
import com.esisalama.tfcproject.model.HistoriqueOperation
import com.esisalama.tfcproject.viewModel.HistoriqueTransactionViewModel

class DetailHistoriqueActivity : AppCompatActivity() {
    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityDetailHistoriqueBinding>(this, R.layout.activity_detail_historique)
    }

    private val mViewModel by lazy {
        ViewModelProviders.of(this).get(HistoriqueTransactionViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val operation = intent.extras?.getParcelable<HistoriqueOperation>("operation")
        mViewModel.getDestinationName(operation?.uidDestination ?: "null")
        mViewModel.getSouceName(operation?.uidSource ?: "dfd")

        binding.operation = operation
        mViewModel.destination.observe(this, Observer {
            binding.destination = it
        })

        mViewModel.source.observe(this, Observer {
            binding.souce = it
        })
    }
}
