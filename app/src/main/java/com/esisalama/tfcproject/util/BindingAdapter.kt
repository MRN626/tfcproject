package com.esisalama.tfcproject.util

import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.experimental.and

@BindingAdapter(value = ["setAdapter"])
fun RecyclerView.bindRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
    this.run {
        this.setHasFixedSize(true)
        this.adapter = adapter
    }
}


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.writeNfc(intent: Intent, data: String): Boolean {
    val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
    val ndefRecord = NdefRecord.createTextRecord(null, data)
    val ndefMessage = NdefMessage(arrayOf(ndefRecord))

    try {

        if (tag != null) {

            val ndef = Ndef.get(tag)
            if (ndef == null) {
                try {
                    val ndefFormat = NdefFormatable.get(tag)

                    if (ndefFormat != null) {
                        ndefFormat.connect()
                        ndefFormat.format(ndefMessage)
                        ndefFormat.close()
                        return true
                    }

                } catch (e: Exception) {
                    return false
                }

            } else {
                ndef.connect()

                if (ndef.isWritable) {
                    ndef.writeNdefMessage(ndefMessage)

                    ndef.close()
                    return true
                }

                ndef.close()
            }
        }

    } catch (e: Exception) {
        return false
    }

    return false
}

fun Context.readFromNfc(intent: Intent): String? {
    val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) ?: return null
    val msg = rawMsgs[0] as NdefMessage
    val payload = msg.records[0].payload

    return try {
        val languageCode = String(payload, 1, (payload[0] and java.lang.Byte.parseByte("00111111", 2)).toInt(), Charset.defaultCharset())
        String(payload, languageCode.length + 1, payload.size - languageCode.length - 1)
    } catch (e: UnsupportedEncodingException) {
        null
    }
}