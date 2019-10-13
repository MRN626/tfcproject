package com.esisalama.tfcproject.view

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.adapter.ProductCartAdapter
import com.esisalama.tfcproject.adapter.common.CustomClick
import com.esisalama.tfcproject.databinding.ActivityNfcCardInfoBinding
import com.esisalama.tfcproject.model.Account
import com.esisalama.tfcproject.model.HistoriqueOperation
import com.esisalama.tfcproject.model.Operation
import com.esisalama.tfcproject.model.ProductCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import org.jetbrains.anko.longToast

@Suppress("DEPRECATION")
class NfcCardInfoActivity : AppCompatActivity(), CustomClick<ProductCart> {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityNfcCardInfoBinding>(this, R.layout.activity_nfc_card_info)
    }

    override fun onItemClick(item: ProductCart, view: View) {
        // This action is not allow here
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productCartAdapter = ProductCartAdapter(this)
        binding.adapter = productCartAdapter

        val gson = GsonBuilder().create()
        val intentExtra = intent.extras?.getString("nfc-info")

        try {
            val operation = gson.fromJson(intentExtra, Operation::class.java)
            productCartAdapter.submitList(operation.productCart)
            binding.totalPrice = operation.productCart.sumByDouble { it.product!!.price }

            binding.btnConfirmation.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Confirmer")
                    .setMessage("Etes vous sur de vouloir continuez ?")
                    .setNegativeButton("Non") { a, b -> }
                    .setPositiveButton("Oui") { a, b -> startTransaction(operation) }
                    .show()
            }

        } catch (e: Exception) {
            binding.totalPrice = 0.0
            longToast("Format des donnees non prise en charge")
        }
    }

    private fun startTransaction(facture: Operation) {
        val totalAmount = facture.productCart.map { it.product!!.price }.sum()

        val uidSouce = FirebaseAuth.getInstance().currentUser?.uid ?: "- - -"
        val sourceRef = firestore
            .collection("accounts")
            .document(uidSouce)
            .get()

        sourceRef.addOnSuccessListener {
            val account = it.toObject(Account::class.java) ?: return@addOnSuccessListener
            if (account.sold < totalAmount) {
                showSoldInsuffisantDialog()
            } else {
                processTransaction(facture, totalAmount)
            }
        }

        sourceRef.addOnFailureListener {
            Toast.makeText(baseContext, it.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun processTransaction(facture: Operation, totalAmount: Double) {
        val progressDialog = ProgressDialog(this).apply {
            setTitle("Chargement")
            setMessage("Operation en cour d'execution")
            setCancelable(false)
            show()
        }

        val destinationRef = firestore
            .collection("accounts")
            .document(facture.uidAccount)

        val uidSource = FirebaseAuth.getInstance().currentUser?.uid ?: "- - -"
        val sourceRef = firestore
            .collection("accounts")
            .document(uidSource)

        val transactionRef = firestore.collection("historique-transaction")
            .document()

        val databaseTransaction = firestore.runTransaction {
            // Recuperation des elements modifier
            val destinationAccount = it.get(destinationRef).toObject(Account::class.java)
            val sourceAccount = it.get(sourceRef).toObject(Account::class.java)


            val newDestinationSold = destinationAccount!!.sold + totalAmount
            val newSourceSold = sourceAccount!!.sold - totalAmount

            val historyTransaction = HistoriqueOperation(
                uid = transactionRef.id,
                uidSource = sourceAccount.uid,
                uidDestination = destinationAccount.uid,
                montant = totalAmount
            )

            // Todo: effectuer la transaction ici

            // Transaction operation
            it.update(destinationRef, "sold", newDestinationSold)
            it.update(sourceRef, "sold", newSourceSold)
            it.set(transactionRef, historyTransaction)

            null
        }

        databaseTransaction.addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(baseContext, "Success", Toast.LENGTH_LONG).show()
            finish()
        }

        databaseTransaction.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(baseContext, it.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showSoldInsuffisantDialog() {
        AlertDialog.Builder(this)
            .setTitle("Message")
            .setMessage("Le solde de votre compte est insufisant")
            .setPositiveButton("Ok") { _, _ -> }
            .show()
    }
}
