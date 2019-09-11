package com.esisalama.tfcproject.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {
    private val database by lazy {
        FirebaseFirestore.getInstance()
    }

}