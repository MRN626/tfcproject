package com.esisalama.tfcproject.viewModel

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.esisalama.tfcproject.model.ProductCart
import com.google.gson.GsonBuilder
import java.lang.Exception

class ProductViewModel(private val app: Application) : AndroidViewModel(app),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(app)
    }

    private val gson by lazy {
        GsonBuilder().create()
    }

    private val _productCart = MutableLiveData<List<ProductCart>>()
    val productCart: LiveData<List<ProductCart>>
        get() = _productCart

    val totalPrice: LiveData<Double> = Transformations.map(productCart) { items ->
        return@map if (items.isNotEmpty()) {
            items.map { it.quantity * it.product!!.price }.sum()
        } else {
            0.0
        }
    }

    init {
        val rawData = sharedPreferences.getString(PRODUCT_CART, "") ?: ""
        val data = getDataFromJson(rawData)
        _productCart.value = data
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(pref: SharedPreferences, key: String) {
        val rawData = sharedPreferences.getString(PRODUCT_CART, "") ?: return
        val data = getDataFromJson(rawData)
        _productCart.value = data
    }

    fun add(productCart: ProductCart) {
        val rawData = sharedPreferences.getString(PRODUCT_CART, "") ?: return
        val data = getDataFromJson(rawData)

        data.add(productCart)
        updateSharedPref(data)
    }

    fun remove(productCart: ProductCart) {
        val rawData = sharedPreferences.getString(PRODUCT_CART, "") ?: return
        val data = getDataFromJson(rawData)

        data.remove(productCart)
        updateSharedPref(data)
    }

    fun removeAll() {
        sharedPreferences.edit {
            clear()
        }
    }

    private fun getDataFromJson(json: String): MutableList<ProductCart> {
        return if (json != "") {
            try {
                gson.fromJson(json, Array<ProductCart>::class.java).toMutableList()
            } catch (e: Exception) {
                mutableListOf<ProductCart>()
            }

        } else {
            mutableListOf<ProductCart>()
        }
    }

    private fun updateSharedPref(data: List<ProductCart>) {
        val rawData = gson.toJson(data)
        sharedPreferences.edit {
            putString(PRODUCT_CART, rawData)
        }
    }

    override fun onCleared() {
        super.onCleared()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {
        const val PRODUCT_CART = "PRODUCT_CART"
    }
}