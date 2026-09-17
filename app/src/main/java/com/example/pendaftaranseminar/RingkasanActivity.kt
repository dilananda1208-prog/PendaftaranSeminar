package com.example.pendaftaranseminar

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RingkasanActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAMA     = "EXTRA_NAMA"
        const val EXTRA_EMAIL    = "EXTRA_EMAIL"
        const val EXTRA_WA       = "EXTRA_WA"
        const val EXTRA_KATEGORI = "EXTRA_KATEGORI"
        const val EXTRA_SESI     = "EXTRA_SESI"
        const val EXTRA_FAKULTAS = "EXTRA_FAKULTAS"
        const val EXTRA_METODE   = "EXTRA_METODE"
        const val EXTRA_JUDUL    = "EXTRA_JUDUL"
        const val EXTRA_JUMLAH   = "EXTRA_JUMLAH" // <--- DITAMBAHKAN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringkasan)

        val nama     = intent.getStringExtra(EXTRA_NAMA)     ?: "-"
        val email    = intent.getStringExtra(EXTRA_EMAIL)    ?: "-"
        val wa       = intent.getStringExtra(EXTRA_WA)       ?: "-"
        val kategori = intent.getStringExtra(EXTRA_KATEGORI) ?: "-"
        val sesi     = intent.getStringExtra(EXTRA_SESI)     ?: "-"
        val fakultas = intent.getStringExtra(EXTRA_FAKULTAS) ?: "-"
        val metode   = intent.getStringExtra(EXTRA_METODE)   ?: "Belum dipilih"
        val judul    = intent.getStringExtra(EXTRA_JUDUL)    ?: "-"
        val jumlah   = intent.getStringExtra(EXTRA_JUMLAH)   ?: "1 orang" // <--- DITAMBAHKAN

        findViewById<TextView>(R.id.tvNama).text = nama
        findViewById<TextView>(R.id.tvDetail).text = buildString {
            appendLine("Judul Makalah: $judul")
            appendLine("Jumlah       : $jumlah") // <--- DITAMBAHKAN
            appendLine("Email        : $email")
            appendLine("WhatsApp     : $wa")
            appendLine("Kategori     : $kategori")
            appendLine("Sesi         : $sesi")
            appendLine("Fakultas     : $fakultas")
            append("Pembayaran   : $metode")
        }

        findViewById<Button>(R.id.btnKembali).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnBagikan).setOnClickListener {
            val ringkasan = buildString {
                appendLine("Pendaftaran Seminar")
                appendLine("Nama     : $nama")
                appendLine("Judul    : $judul")
                appendLine("Jumlah   : $jumlah") // <--- DITAMBAHKAN
                appendLine("Email    : $email")
                appendLine("Kategori : $kategori")
                appendLine("Sesi     : $sesi")
                append("Fakultas : $fakultas")
            }

            val bagikan = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, ringkasan)
            }

            try {
                startActivity(Intent.createChooser(bagikan, "Bagikan ringkasan lewat"))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Tidak ada aplikasi untuk berbagi", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnHubungi).setOnClickListener {
            val nomorPanitia = "081234567890"
            val panggilIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$nomorPanitia")
            }
            startActivity(panggilIntent)
        }
    }
}