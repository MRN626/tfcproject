package com.esisalama.tfcproject.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esisalama.tfcproject.model.Account
import com.esisalama.tfcproject.model.HistoriqueOperation
import com.esisalama.tfcproject.util.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoriqueTransactionViewModel : ViewModel() {

    private val firebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentUserUid by lazy {
        FirebaseAuth.getInstance().currentUser?.uid ?: "null"
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _historique = MutableLiveData<List<HistoriqueOperation>>()
    val historique: LiveData<List<HistoriqueOperation>>
        get() = _historique

    private val _source = MutableLiveData<String>()
    val source: LiveData<String>
        get() = _source

    private val _destination = MutableLiveData<String>()
    val destination: LiveData<String>
        get() = _destination

    init {
        getTransaction()
    }

    fun getSouceName(uid: String) {
        val accountRef = firebaseFirestore.collection("accounts").document(uid)
        accountRef.addSnapshotListener { snapshot, firestoreException ->
            if (firestoreException != null || snapshot == null) {
                return@addSnapshotListener
            }

            _source.value = snapshot.toObject(Account::class.java)?.ownerName ?: "- - -"
        }
    }

    fun getDestinationName(uid: String) {
        val accountRef = firebaseFirestore.collection("accounts").document(uid)
        accountRef.addSnapshotListener { snapshot, firestoreException ->
            if (firestoreException != null || snapshot == null) {
                return@addSnapshotListener
            }

            _destination.value = snapshot.toObject(Account::class.java)?.ownerName ?: "- - -"
        }
    }

    private fun getTransaction() {
        val collectionReference = firebaseFirestore.collection("historique-transaction")

        _loadingState.value = LoadingState.LOADING
        collectionReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                // Loading error
                _loadingState.value = LoadingState.error(exception?.message)
                return@addSnapshotListener
            }

            _historique.value = snapshot.toObjects(HistoriqueOperation::class.java).filter {
                (it.uidDestination == currentUserUid) || (it.uidSource == currentUserUid)
            }
            _loadingState.value = LoadingState.LOADED
        }
    }
}