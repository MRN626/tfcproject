package com.esisalama.tfcproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.esisalama.tfcproject.adapter.common.CustomClick
import com.esisalama.tfcproject.adapter.common.CustomViewHolder
import com.esisalama.tfcproject.databinding.ItemProductCartBinding
import com.esisalama.tfcproject.model.ProductCart

class ProductCartAdapter(private val listener: CustomClick<ProductCart>) :
    ListAdapter<ProductCart, CustomViewHolder>(Companion) {

    private val colorGenerator = ColorGenerator.MATERIAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductCartBinding.inflate(inflater, parent, false)

        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = getItem(position) ?: return
        val productCartBinding = holder.binding as ItemProductCartBinding

        productCartBinding.run {
            view.setBackgroundColor(colorGenerator.getColor(currentItem))
            productCart = currentItem
            btnMore.setOnClickListener {
                listener.onItemClick(currentItem, productCartBinding.root)
            }
        }
    }

    companion object : DiffUtil.ItemCallback<ProductCart>() {
        override fun areItemsTheSame(oldItem: ProductCart, newItem: ProductCart): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ProductCart, newItem: ProductCart): Boolean {
            return oldItem == newItem
        }
    }
}