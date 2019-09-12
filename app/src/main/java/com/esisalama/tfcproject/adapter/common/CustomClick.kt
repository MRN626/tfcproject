package com.esisalama.tfcproject.adapter.common

import android.view.View

interface CustomClick<T> {
    fun onItemClick(item: T, view: View)
}