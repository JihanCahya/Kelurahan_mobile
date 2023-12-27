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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.uas.sipkburengan.databinding.ActivityEditKeteranganBinding
import com.polinema.uas.sipkburengan.databinding.ActivityEditKkBinding

class EditSuratKeteranganActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b: ActivityEditKeteranganBinding
    private lateinit var db: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var suratId: String
    lateinit var adapterSpin: ArrayAdapter<String>
    val arrayStatusKeterangan = arrayOf("Keterangan Menikah", "Keterangan Pindah", "Keterangan Cerai", "Keterangan Kematian")
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
    private lateinit var oldImageUrlKTP: String
    private lateinit var oldImageUrlPengantarRT: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditKeteranganBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        storageReference = FirebaseStorage.getInstance().reference
        suratId = intent.getStringExtra("ID_SURAT") ?: ""

        adapterSpin = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayStatusKeterangan)
        b.spEdKet.adapter = adapterSpin
        b.spEdKet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle item selection if needed
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }

        // Set click listeners for buttons
        b.btnSimpanEdKet.setOnClickListener(this)
        b.edFcKtpKet.setOnClickListener(this)
        b.edPengantarRtKet.setOnClickListener(this)

        // Load data saat aktivitas dimulai
        loadDetailKK()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSimpanEdKet -> {
                if (imageUriKtp != null || imageUriPengantarRt != null) {
                    // Check if KTP image is selected
                    if (imageUriKtp != null) {
                        uploadAndUpdateImage("ktp", imageUriKtp!!)
                    }

                    // Check if Pengantar RT image is selected
                    if (imageUriPengantarRt != null) {
                        uploadAndUpdateImage("pengantar_rt", imageUriPengantarRt!!)
                    }
                } else {
                    // No new images selected, update other fields and save the updated History object
                    val suratKK = createHistoryObject()
                    suratKK.status = "Sudah diperbarui"
                    updateDatabase(suratKK)
                }
            }
            R.id.edFcKtp_Ket -> {
                // Open the file picker for KTP
                openFileChooserKtp()
            }
            R.id.edPengantarRt_Ket -> {
                // Open the file picker for Pengantar RT
                openFileChooserPengantarRt()
            }
        }
    }
    private fun uploadAndUpdateImage(imageType: String, imageUri: Uri) {
        val timestamp = System.currentTimeMillis()

        // Upload the selected image
        uploadImage(imageType, imageUri, timestamp) { imageUrl ->
            // Update the corresponding field in the History object
            when (imageType) {
                "ktp" -> imageUrlKTP = imageUrl
                "pengantar_rt" -> imageUrlPengantarRT = imageUrl
            }

            // Update other fields and save the updated History object
            val suratKK = createHistoryObject()
            suratKK.status = "Sudah diperbarui"
            updateDatabase(suratKK)
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

        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST_KTP -> {
                    imageUriKtp = data.data
                    // Load the selected image using Glide or your preferred image-loading library
                    Glide.with(this@EditSuratKeteranganActivity)
                        .load(imageUriKtp)
                        .into(b.imEdKtpKet)
                }
                PICK_IMAGE_REQUEST_PENGANTAR_RT -> {
                    imageUriPengantarRt = data.data
                    // Load the selected image using Glide or your preferred image-loading library
                    Glide.with(this@EditSuratKeteranganActivity)
                        .load(imageUriPengantarRt)
                        .into(b.imEdPengantarKet)
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
                            oldImageUrlKTP = it.imageUrlKTP  // Store the old KTP image URL
                            oldImageUrlPengantarRT = it.imageUrlPengantarRT  // Store the old Pengantar RT image URL
                            status = it.status
                            nama_pengaju = it.nama_pengaju
                            nama_surat = it.surat
                            tanggalPengajuan = it.tanggalPengajuan
                            tanggalSelesai = it.tanggalSelesai
                            keterangan = it.keterangan

                            // load image
                            Glide.with(this@EditSuratKeteranganActivity)
                                .load(it.imageUrlKTP)
                                .into(imEdKtpKet)

                            Glide.with(this@EditSuratKeteranganActivity)
                                .load(it.imageUrlPengantarRT)
                                .into(imEdPengantarKet)

                            val position = arrayStatusKeterangan.indexOf(it.jenisSurat)
                            spEdKet.setSelection(position)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error, if any.
            }
        })
    }

    private fun uploadImage(imageType: String, imageUri: Uri, timestamp: Long, onComplete: (String) -> Unit) {
        val fileName = "$suratId-$imageType-$timestamp.jpg"
        val imageRef = storageReference.child("images/pengajuan_keterangan/$fileName")

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                onComplete(imageUrl)
            }
        }.addOnFailureListener { exception ->
            showErrorDialog("Terjadi kesalahan saat mengunggah gambar: ${exception.localizedMessage}")
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }

    private fun updateDatabase(suratKK: History) {
        db.child(suratId).setValue(suratKK).addOnSuccessListener {
            AlertDialog.Builder(this).apply {
                setTitle("BERHASIL")
                setMessage("Data berhasil diperbarui !")
                setPositiveButton("Ya") { _, _ -> finish() }
            }.create().show()
        }
    }

    private fun createHistoryObject(): History {
        return History(
            suratId, id_pengaju, nama_pengaju, nama_surat,
            adapterSpin.getItem(b.spEdKet.selectedItemPosition)!!, status,
            tanggalPengajuan, keterangan, imageUrlAkta, imageUrlKK, imageUrlKTP, imageUrlPengantarRT, tanggalSelesai
        )
    }

    // Constants for image selection
    companion object {
        private const val PICK_IMAGE_REQUEST_KTP = 1
        private const val PICK_IMAGE_REQUEST_PENGANTAR_RT = 2
    }
}
