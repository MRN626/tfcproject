package com.esisalama.tfcproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.adapter.common.CustomClick
import com.esisalama.tfcproject.adapter.common.CustomViewHolder
import com.esisalama.tfcproject.databinding.ItemOperationBinding
import com.esisalama.tfcproject.model.HistoriqueOperation
import com.esisalama.tfcproject.util.getFormattedDate

class HistoriqueOperationAdapter (private val listener: CustomClick<HistoriqueOperation>)
    : ListAdapter<HistoriqueOperation, CustomViewHolder>(Companion) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOperationBinding.inflate(inflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentOperationInTheList = getItem(position) ?: return
        val historiqueOperationBinding = holder.binding as ItemOperationBinding

        historiqueOperationBinding.run {
            formatteDate = currentOperationInTheList.date?.getFormattedDate("E, dd/MM/yyyy à HH:mm") ?: "---"
            operation = currentOperationInTheList
            root.setOnClickListener {
                listener.onItemClick(currentOperationInTheList, it)
            }
        }
    }

    companion object : DiffUtil.ItemCallback<HistoriqueOperation>() {
        override fun areItemsTheSame(oldItem: HistoriqueOperation, newItem: HistoriqueOperation): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: HistoriqueOperation, newItem: HistoriqueOperation): Boolean {
            return oldItem == newItem
        }
    }
}