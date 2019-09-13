package com.esisalama.tfcproject.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Transaction(
    val uid: String = "",
    @field:ServerTimestamp val date: Date? = null,
    val montant: Double = 0.0,
    val uidSource: String = "",
    val uidDestination: String = ""
) : Parcelable