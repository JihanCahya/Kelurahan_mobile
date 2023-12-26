package com.polinema.uas.sipkburengan

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.polinema.uas.sipkburengan.databinding.ActivityDetailSuratKtpactivityBinding
import java.util.Calendar

class DetailSuratKTPActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityDetailSuratKtpactivityBinding
    private lateinit var db: DatabaseReference
    private lateinit var suratId: String
    lateinit var adapterSpin : ArrayAdapter<String>
    val arrayStatusKTP = arrayOf("Belum dicek", "Belum terpenuhi", "Terpenuhi", "Sudah diambil")
    lateinit var imageUrlAkta : String
    lateinit var imageUrlKK : String
    lateinit var imageUrlKTP : String
    lateinit var imageUrlPengantarRT : String
    lateinit var jenisSurat : String
    lateinit var nama_pengaju : String
    lateinit var nama_surat : String
    lateinit var tanggalPengajuan : String
    lateinit var id_pengaju : String
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    lateinit var date : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailSuratKtpactivityBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        suratId = intent.getStringExtra("ID_SURAT") ?: ""

        adapterSpin = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayStatusKTP)
        b.spStatusKTP.adapter = adapterSpin

        b.btnKembaliKTP.setOnClickListener(this)
        b.btnSimpanKTP.setOnClickListener(this)

        loadDetailKTP()

        b.spStatusKTP.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = adapterSpin.getItem(position)
                if (status == "Belum terpenuhi"){
                    b.textInputLayout31.visibility = View.VISIBLE
                    b.edKeteranganKTP.setText("-")
                    date = "-"
                } else if (status == "Belum dicek"){
                    b.textInputLayout31.visibility = View.INVISIBLE
                    b.edKeteranganKTP.setText("-")
                    date = "-"
                } else if (status == "Terpenuhi"){
                    b.textInputLayout31.visibility = View.INVISIBLE
                    b.edKeteranganKTP.setText("Dapat diambil setelah 3x24 jam")
                    date = "$day-$month-$year"
                } else if (status == "Sudah diambil"){
                    b.textInputLayout31.visibility = View.INVISIBLE
                    b.edKeteranganKTP.setText("selesai")
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnKembaliKTP -> {
                finish()
            }
            R.id.btnSimpanKTP -> {
                val suratKTP = Validasi(suratId, id_pengaju, nama_pengaju, nama_surat, jenisSurat, adapterSpin.getItem(b.spStatusKTP.selectedItemPosition)!!, b.edKeteranganKTP.text.toString(), tanggalPengajuan, date, imageUrlAkta, imageUrlKK, imageUrlKTP, imageUrlPengantarRT)
                db.child(suratId).setValue(suratKTP).addOnSuccessListener {
                    AlertDialog.Builder(this).apply {
                        setTitle("BERHASIL")
                        setMessage("Data validasi berhasil diperbarui !")
                        setPositiveButton("Ya") { _, _ -> finish() }
                    }.create().show()
                }
            }
        }
    }

    private fun loadDetailKTP() {
        db.child(suratId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val validasi = dataSnapshot.getValue(Validasi::class.java)
                validasi?.let {
                    with(b) {
                        // set variabel
                        imageUrlAkta = it.imageUrlAkta
                        imageUrlKK = it.imageUrlKK
                        imageUrlKTP = it.imageUrlKTP
                        imageUrlPengantarRT = it.imageUrlPengantarRT
                        jenisSurat = it.jenisSurat
                        nama_pengaju = it.nama_pengaju
                        nama_surat = it.surat
                        tanggalPengajuan = it.tanggalPengajuan
                        id_pengaju = it.id_pengaju
                        date = it.tanggalSelesai

                        // load image
                        Glide.with(this@DetailSuratKTPActivity)
                            .load(validasi.imageUrlAkta)
                            .into(imFotoAkta)

                        Glide.with(this@DetailSuratKTPActivity)
                            .load(validasi.imageUrlKTP)
                            .into(imFotoKTP)

                        Glide.with(this@DetailSuratKTPActivity)
                            .load(validasi.imageUrlKK)
                            .into(imFotoKK)

                        Glide.with(this@DetailSuratKTPActivity)
                            .load(validasi.imageUrlPengantarRT)
                            .into(imFotoRT)

                        b.imFotoAkta.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imageUrlAkta))
                            startActivity(intent)
                        }
                        b.imFotoKTP.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imageUrlKTP))
                            startActivity(intent)
                        }
                        b.imFotoKK.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imageUrlKK))
                            startActivity(intent)
                        }
                        b.imFotoRT.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imageUrlPengantarRT))
                            startActivity(intent)
                        }

                        b.edKeteranganKTP.setText(it.keterangan)
                        val position = arrayStatusKTP.indexOf(it.status)
                        b.spStatusKTP.setSelection(position)
                    }
                }
            }
        }
    }
}