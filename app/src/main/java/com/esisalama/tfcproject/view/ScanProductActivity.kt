package com.esisalama.tfcproject.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.esisalama.tfcproject.R
import com.esisalama.tfcproject.model.Product
import com.esisalama.tfcproject.model.ProductCart
import com.esisalama.tfcproject.viewModel.ProductViewModel
import com.google.gson.GsonBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.kinda.alert.KAlertDialog
import kotlinx.android.synthetic.main.activity_scanne_product.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.*
import kotlin.properties.Delegates

class ScanProductActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var dialog: KAlertDialog by Delegates.notNull()
    private val mViewModel by viewModels<ProductViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanne_product)
        setScannerProperties()

        btnLaunchScanner.setOnClickListener {
            reloadScanner()
        }
    }

    private fun reloadScanner() {
        qrCodeScanner.run {
            stopCamera()
            startCamera()
            setResultHandler(this@ScanProductActivity)
        }
    }

    override fun handleResult(result: Result?) {
        if (result != null) {
            dialog = KAlertDialog(this, KAlertDialog.PROGRESS_TYPE).apply {
                titleText = "Verification !"
                contentText = "Verification en cour d'execution..."
                show()
            }

            dialog.setConfirmClickListener {
                val dialog = it.alerType == KAlertDialog.WARNING_TYPE
                        || it.alerType == KAlertDialog.ERROR_TYPE
                        || it.alerType == KAlertDialog.SUCCESS_TYPE

                if (dialog) {
                    it.dismissWithAnimation()
                    reloadScanner()
                }
            }

            getDataFromQrCode(result)
        }
    }

    private fun getDataFromQrCode(result: Result) {
        val productJson = result.text.toString()
        val gson = GsonBuilder().create()

        try {
            val product = gson.fromJson(productJson, Product::class.java)
            val productCart = ProductCart(
                uid = UUID.randomUUID().toString(),
                product = product,
                quantity = 1
            )

            mViewModel.add(productCart)
            dialog.titleText = "Success"
            dialog.changeAlertType(KAlertDialog.SUCCESS_TYPE)

        } catch (e: Exception) {
            dialog.changeAlertType(KAlertDialog.ERROR_TYPE)
            dialog.titleText = "Erreur"
            dialog.contentText = e.message
        }
    }

    override fun onPause() {
        super.onPause()
        qrCodeScanner.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat
                    .requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
                return
            }
        }

        qrCodeScanner.run {
            startCamera()
            setResultHandler(this@ScanProductActivity)
        }
    }

    private fun setScannerProperties() {
        qrCodeScanner.run {
            setFormats(listOf(BarcodeFormat.QR_CODE))
            setAutoFocus(true)
            setLaserColor(R.color.colorAccent)
            setMaskColor(R.color.colorAccent)
        }
    }
}
