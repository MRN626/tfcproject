package com.esisalama.tfcproject.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esisalama.tfcproject.model.HistoriqueOperation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoriqueTransactionViewModel : ViewModel() {

    private val firebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentUserUid by lazy {
        FirebaseAuth.getInstance().currentUser?.uid ?: "null"
    }

    private val _historique = MutableLiveData<List<HistoriqueOperation>>()
    val historique: LiveData<List<HistoriqueOperation>>
        get() = _historique

    init {
        getTransaction()
    }

    private fun getTransaction() {
        val collectionReference = firebaseFirestore.collection("historique-transaction")
            .whereEqualTo("uidSource", currentUserUid)
            .whereEqualTo("uidDestination", currentUserUid)

        collectionReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                // Loading error
                return@addSnapshotListener
            }

            _historique.value = snapshot.toObjects(HistoriqueOperation::class.java)
        }
    }
}