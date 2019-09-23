package com.esisalama.tfcproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val uid: String = "",
    val price: Double = 0.0,
    val name: String = ""
) : Parcelable