package com.polinema.uas.sipkburengan

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polinema.uas.sipkburengan.databinding.ActivityEditKkBinding
import java.util.Calendar

class EditSuratKKActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b: ActivityEditKkBinding
    private lateinit var db: DatabaseReference
    private lateinit var suratId: String
    lateinit var adapterSpin: ArrayAdapter<String>
    val arrayJenisKK = arrayOf("KK Baru", "KK Hilang")
    lateinit var imageUrlAkta: String
    lateinit var imageUrlKK: String
    lateinit var imageUrlKTP: String
    lateinit var imageUrlPengantarRT: String
    lateinit var status: String
    lateinit var nama_pengaju: String
    lateinit var nama_surat: String
    lateinit var tanggalPengajuan: String
    lateinit var id_pengaju: String
    lateinit var tanggalSelesai: String
    lateinit var keterangan: String

    // New properties for image selection
    private var imageUriKtp: Uri? = null
    private var imageUriPengantarRt: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditKkBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        suratId = intent.getStringExtra("ID_SURAT") ?: ""

        adapterSpin = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayJenisKK)
        b.spEdKk.adapter = adapterSpin
        b.spEdKk.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle item selection if needed
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }

        // Set click listeners for buttons
        b.btnSimpanEdKk.setOnClickListener(this)
        b.edFcKtpKk.setOnClickListener(this)
        b.edPengantarRtKk.setOnClickListener(this)

        // Load data saat aktivitas dimulai
        loadDetailKK()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSimpanEdKk -> {
                // Check if KTP and Pengantar RT images are selected
                if (imageUriKtp != null && imageUriPengantarRt != null) {
                    // Upload the new images to Firebase Storage or your preferred storage
                    // Update the imageUrlKTP and imageUrlPengantarRT properties in the History object
                    // ...
                }

                // Update other fields and save the updated History object
                val suratKK = History(
                    suratId, id_pengaju, nama_pengaju, nama_surat,
                    adapterSpin.getItem(b.spEdKk.selectedItemPosition)!!,status,
                    tanggalPengajuan, keterangan, imageUrlAkta, imageUrlKK, imageUrlKTP, imageUrlPengantarRT, tanggalSelesai
                )

                db.child(suratId).setValue(suratKK).addOnSuccessListener {
                    AlertDialog.Builder(this).apply {
                        setTitle("BERHASIL")
                        setMessage("Data History berhasil diperbarui !")
                        setPositiveButton("Ya") { _, _ -> finish() }
                    }.create().show()
                }
            }
            R.id.edFcKtp_Kk -> {
                // Open the file picker for KTP
                openFileChooserKtp()
            }
            R.id.edPengantarRt_Kk -> {
                // Open the file picker for Pengantar RT
                openFileChooserPengantarRt()
            }
        }
    }

    private fun openFileChooserKtp() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_KTP)
    }

    private fun openFileChooserPengantarRt() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_PENGANTAR_RT)
    }

    // Handle the result of the image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST_KTP -> {
                    imageUriKtp = data.data
                    // Load the selected image using Glide or your preferred image-loading library
                    Glide.with(this@EditSuratKKActivity)
                        .load(imageUriKtp)
                        .into(b.imageView12)
                }
                PICK_IMAGE_REQUEST_PENGANTAR_RT -> {
                    imageUriPengantarRt = data.data
                    // Load the selected image using Glide or your preferred image-loading library
                    Glide.with(this@EditSuratKKActivity)
                        .load(imageUriPengantarRt)
                        .into(b.imageView17)
                }
            }
        }
    }

    private fun loadDetailKK() {
        db.child(suratId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val history = dataSnapshot.getValue(History::class.java)
                    history?.let {
                        with(b) {
                            // set variabel
                            id_pengaju = it.id_pengaju
                            imageUrlAkta = it.imageUrlAkta
                            imageUrlKK = it.imageUrlKK
                            imageUrlKTP = it.imageUrlKTP
                            imageUrlPengantarRT = it.imageUrlPengantarRT
                            status = it.status
                            nama_pengaju = it.nama_pengaju
                            nama_surat = it.surat
                            tanggalPengajuan = it.tanggalPengajuan
                            tanggalSelesai = it.tanggalSelesai
                            keterangan = it.keterangan

                            // load image
                            Glide.with(this@EditSuratKKActivity)
                                .load(it.imageUrlKTP)
                                .into(imageView12)

                            Glide.with(this@EditSuratKKActivity)
                                .load(it.imageUrlPengantarRT)
                                .into(imageView17)

                            val position = arrayJenisKK.indexOf(it.jenisSurat)
                            spEdKk.setSelection(position)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error, if any.
            }
        })
    }

    // Constants for image selection
    companion object {
        private const val PICK_IMAGE_REQUEST_KTP = 1
        private const val PICK_IMAGE_REQUEST_PENGANTAR_RT = 2
    }
}
