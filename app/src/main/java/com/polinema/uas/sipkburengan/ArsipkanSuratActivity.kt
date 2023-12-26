package com.polinema.uas.sipkburengan

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.uas.sipkburengan.databinding.ActivityArsipkanSuratBinding

class ArsipkanSuratActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityArsipkanSuratBinding
    private lateinit var db: DatabaseReference
    private lateinit var storage: StorageReference
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var suratId: String
    lateinit var imageUrlAkta : String
    lateinit var imageUrlKK : String
    lateinit var imageUrlKTP : String
    lateinit var imageUrlPengantarRT : String
    lateinit var jenisSurat : String
    lateinit var nama_pengaju : String
    lateinit var nama_surat : String
    lateinit var tanggalPengajuan : String
    lateinit var tanggalSelesai : String
    lateinit var id_pengaju : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityArsipkanSuratBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        storage = FirebaseStorage.getInstance().reference
        suratId = intent.getStringExtra("ID_SURAT") ?: ""

        b.btnUnggahArsip.setOnClickListener(this)
        b.btnSimpanArsip.setOnClickListener(this)
        b.btnKembaliArsip.setOnClickListener(this)

        loadDataSurat()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnUnggahArsip -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
            R.id.btnSimpanArsip -> {
                val newRef = db.push()
                val uniqueKey = newRef.key

                if (imageUri != null){
                    b.progressBar9.visibility = View.VISIBLE
                    val imageRef = storage.child("arsip/$uniqueKey.jpg")
                    val uploadTask = imageRef.putFile(imageUri!!)

                    uploadTask.addOnSuccessListener{ taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()

                            val arsip = Arsip(suratId, id_pengaju, nama_pengaju, nama_surat, jenisSurat, "Arsip", "-", tanggalPengajuan, tanggalSelesai, imageUrlAkta, imageUrlKK, imageUrlKTP, imageUrlPengantarRT, imageUrl)
                            val arsipRef = db.child(suratId)
                            arsipRef.setValue(arsip)

                            b.progressBar9.visibility = View.GONE
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("BERHASIL")
                            builder.setMessage("Data arsip berhasil diunggah !")
                            builder.setPositiveButton("Ya") { _, _ ->
                                finish()
                            }
                            val dialog = builder.create()
                            dialog.show()
                        }
                    }.addOnFailureListener{ exception ->
                        b.progressBar9.visibility = View.GONE
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("ERROR")
                        builder.setMessage("Terjadi kesalahan saat mengunggah gambar: ${exception.message}")
                        builder.setPositiveButton("Ya") { _, _ -> }
                        val dialog = builder.create()
                        dialog.show()
                    }
                } else {
                    Toast.makeText(this, "Foto harus diunggah !!!", Toast.LENGTH_LONG).show()
                }
            }
            R.id.btnKembaliArsip -> {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            b.tvFotoArsip.text = "Foto telah diunggah"
        }
    }

    private fun loadDataSurat() {
        db.child(suratId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val arsip = dataSnapshot.getValue(Arsip::class.java)
                arsip?.let {
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
                        tanggalSelesai = it.tanggalSelesai
                    }
                }
            }
        }
    }
}

// =======================================================
data class Arsip(
    val id: String,
    val id_pengaju : String,
    val nama_pengaju: String,
    val surat: String,
    val jenisSurat: String,
    val status: String,
    val keterangan : String,
    var tanggalPengajuan: String,
    var tanggalSelesai: String,
    var imageUrlAkta: String,
    var imageUrlKK: String,
    var imageUrlKTP: String,
    var imageUrlPengantarRT: String,
    var imageArsip: String
) {
    constructor() : this( "","","", "","", "","","", "", "", "", "","","")
}