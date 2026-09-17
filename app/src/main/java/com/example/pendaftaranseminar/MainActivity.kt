package com.example.pendaftaranseminar

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var tilNama: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilWa: TextInputLayout
    private lateinit var tilJudul: TextInputLayout
    private lateinit var etNama: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etWa: TextInputEditText
    private lateinit var etJudul: TextInputEditText
    private lateinit var rgKategori: RadioGroup
    private lateinit var cbAi: CheckBox
    private lateinit var cbIot: CheckBox
    private lateinit var cbCloud: CheckBox
    private lateinit var spFakultas: Spinner
    private lateinit var spJumlah: Spinner // <--- DITAMBAHKAN
    private lateinit var swSetuju: SwitchCompat

    private var metodeTerpilih = "Belum dipilih"

    private val pilihBayarLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { hasil ->
            if (hasil.resultCode == RESULT_OK) {
                metodeTerpilih = hasil.data?.getStringExtra(BayarActivity.EXTRA_METODE) ?: "Belum dipilih"
                findViewById<TextView>(R.id.tvMetode).text = "Metode pembayaran: $metodeTerpilih"
            } else {
                Toast.makeText(this, "Pemilihan dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tilNama = findViewById(R.id.tilNama)
        tilEmail = findViewById(R.id.tilEmail)
        tilWa = findViewById(R.id.tilWa)
        tilJudul = findViewById(R.id.tilJudul)
        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etWa = findViewById(R.id.etWa)
        etJudul = findViewById(R.id.etJudul)
        rgKategori = findViewById(R.id.rgKategori)
        cbAi = findViewById(R.id.cbAi)
        cbIot = findViewById(R.id.cbIot)
        cbCloud = findViewById(R.id.cbCloud)
        spFakultas = findViewById(R.id.spFakultas)
        spJumlah = findViewById(R.id.spJumlah) // <--- DITAMBAHKAN
        swSetuju = findViewById(R.id.swSetuju)
        val btnDaftar = findViewById<Button>(R.id.btnDaftar)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val btnBayar = findViewById<Button>(R.id.btnBayar)

        // Adapter Spinner Fakultas
        ArrayAdapter.createFromResource(
            this,
            R.array.fakultas_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spFakultas.adapter = adapter
        }

        // Adapter Spinner Jumlah Peserta (DITAMBAHKAN)
        ArrayAdapter.createFromResource(
            this,
            R.array.jumlah_peserta_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spJumlah.adapter = adapter
        }

        cbAi.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                "AI & Machine Learning: ${if (isChecked) "dipilih" else "dibatalkan"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        swSetuju.setOnCheckedChangeListener { _, isChecked ->
            btnDaftar.isEnabled = isChecked
            if (isChecked) {
                Toast.makeText(this, "Terima kasih, tombol daftar aktif", Toast.LENGTH_SHORT).show()
            }
        }

        btnBayar.setOnClickListener {
            val niat = Intent(this, BayarActivity::class.java)
            pilihBayarLauncher.launch(niat)
        }

        btnDaftar.setOnClickListener {
            if (!formValid()) return@setOnClickListener

            val pindah = Intent(this, RingkasanActivity::class.java).apply {
                putExtra(RingkasanActivity.EXTRA_NAMA, etNama.text.toString().trim())
                putExtra(RingkasanActivity.EXTRA_JUDUL, etJudul.text.toString().trim())
                putExtra(RingkasanActivity.EXTRA_EMAIL, etEmail.text.toString().trim())
                putExtra(RingkasanActivity.EXTRA_WA, etWa.text.toString().trim())
                putExtra(RingkasanActivity.EXTRA_KATEGORI, kategoriTerpilih())
                putExtra(RingkasanActivity.EXTRA_SESI, sesiTerpilih().joinToString(", "))
                putExtra(RingkasanActivity.EXTRA_FAKULTAS, spFakultas.selectedItem.toString())
                putExtra(RingkasanActivity.EXTRA_JUMLAH, spJumlah.selectedItem.toString()) // <--- DITAMBAHKAN
                putExtra(RingkasanActivity.EXTRA_METODE, metodeTerpilih)
            }

            startActivity(pindah)
        }

        btnReset.setOnClickListener {
            etNama.text?.clear()
            etJudul.text?.clear()
            etEmail.text?.clear()
            etWa.text?.clear()
            rgKategori.check(R.id.rbMahasiswa)
            cbAi.isChecked = false
            cbIot.isChecked = false
            cbCloud.isChecked = false
            spFakultas.setSelection(0)
            spJumlah.setSelection(0) // <--- DITAMBAHKAN
            swSetuju.isChecked = false
            metodeTerpilih = "Belum dipilih"
            findViewById<TextView>(R.id.tvMetode).text = "Metode pembayaran: Belum dipilih"
            Toast.makeText(this, "Form dibersihkan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formValid(): Boolean {
        tilNama.error = null
        tilJudul.error = null
        tilEmail.error = null
        tilWa.error = null

        val nama = etNama.text.toString().trim()
        val judul = etJudul.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val wa = etWa.text.toString().trim()

        if (nama.isEmpty()) {
            tilNama.error = getString(R.string.err_nama)
            etNama.requestFocus()
            return false
        }

        if (judul.isEmpty()) {
            tilJudul.error = "Judul makalah tidak boleh kosong"
            etJudul.requestFocus()
            return false
        } else if (judul.length < 5) {
            tilJudul.error = "Judul makalah minimal 5 karakter"
            etJudul.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.err_email)
            etEmail.requestFocus()
            return false
        }

        if (wa.length < 10) {
            tilWa.error = getString(R.string.err_wa)
            etWa.requestFocus()
            return false
        }

        if (!cbAi.isChecked && !cbIot.isChecked && !cbCloud.isChecked) {
            Toast.makeText(this, R.string.err_sesi, Toast.LENGTH_SHORT).show()
            return false
        }

        if (!swSetuju.isChecked) {
            Toast.makeText(this, R.string.err_setuju, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun kategoriTerpilih(): String {
        return when (rgKategori.checkedRadioButtonId) {
            R.id.rbMahasiswa -> "Mahasiswa"
            R.id.rbDosen     -> "Dosen"
            R.id.rbUmum      -> "Umum"
            else             -> "-"
        }
    }

    private fun sesiTerpilih(): List<String> {
        val daftar = mutableListOf<String>()
        if (cbAi.isChecked)    daftar.add("AI & Machine Learning")
        if (cbIot.isChecked)   daftar.add("Internet of Things")
        if (cbCloud.isChecked) daftar.add("Cloud Computing")
        return daftar
    }
}