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
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esisalama.tfcproject.R
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.and

@BindingAdapter(value = ["setAdapter"])
fun RecyclerView.bindRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
    this.run {
        this.setHasFixedSize(true)
        this.adapter = adapter
    }
}

fun String.getDateFromString(): Date? {
    val regex = """^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$""".toRegex()

    return if (regex.matches(this)) {
        SimpleDateFormat("dd/mm/yyyy", Locale.FRANCE).parse(this)
    } else {
        null
    }
}

fun Date.getFormattedDate(format: String): String? {
    return try {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        formatter.format(this)
    } catch (exp: Exception) {
        null
    }
}

@BindingAdapter(value = ["bindDateAndTime"])
fun TextView.bindDateAndTime(date: Date?) {
    text = if (date == null) {
        "- - -"
    } else {
        date.getFormattedDate(context.getString(R.string.datetime_format))
    }
}

fun String.getTimeFromString(): Date? {
    val regex = """([01]?[0-9]|2[0-3]):[0-5][0-9]""".toRegex()

    return if (regex.matches(this)) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).parse(this)
    } else {
        null
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
                    Log.e("ericampire", "${e.message}")
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
        Log.e("ericampire", "${e.message}")
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