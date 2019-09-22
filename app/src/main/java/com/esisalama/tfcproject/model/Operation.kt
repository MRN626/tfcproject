package com.esisalama.tfcproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Operation(
    val uidAccount: String,
    val productCart: List<ProductCart>
) : Parcelable