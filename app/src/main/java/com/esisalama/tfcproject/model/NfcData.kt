package com.esisalama.tfcproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class NfcData(
    val uid: String = "",
    val accound: Account,
    val productCart: List<ProductCart>
) : Parcelable