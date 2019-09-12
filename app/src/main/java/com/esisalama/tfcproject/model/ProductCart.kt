package com.esisalama.tfcproject.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ProductCart(
    val uid: String = "",
    val product: Product? = null,
    val quantity: Int = 0,
    @field:ServerTimestamp val createAt: Date? = null
) : Parcelable