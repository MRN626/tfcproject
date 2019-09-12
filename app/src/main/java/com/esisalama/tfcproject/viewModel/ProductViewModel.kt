package com.esisalama.tfcproject.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esisalama.tfcproject.model.ProductCart

class ProductViewModel : ViewModel() {
    private val _productCart = MutableLiveData<List<ProductCart>>()
    val productCart: LiveData<List<ProductCart>>
        get() = _productCart

    fun add(productCart: ProductCart) {

    }

    fun remove(productCart: ProductCart) {

    }

    fun removeAll() {

    }
}