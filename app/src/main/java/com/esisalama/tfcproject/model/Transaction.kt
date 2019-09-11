package com.esisalama.tfcproject.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Transaction(
    val uid: String = "",
    @field:ServerTimestamp val date: Date? = null
)