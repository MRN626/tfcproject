package com.esisalama.tfcproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
    val uid: String = "",
    val ownerName: String = "",
    val sold: Double = 0.0
) : Parcelable