package com.polinema.uas.sipkburengan

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.uas.sipkburengan.databinding.ActivityEditInformasiBinding

class EditInformasiActivity : AppCompatActivity(), View.OnClickListener {
    
    private lateinit var b : ActivityEditInformasiBinding
    private lateinit var db: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var informasiId: String
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 2
    lateinit var adapterSpin : ArrayAdapter<String>
    val arrayInformasi = arrayOf("Berita kelurahan", "Informasi bantuan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditInformasiBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("informasi")
        storage = FirebaseStorage.getInstance().reference

        informasiId = intent.getStringExtra("ID_INFORMASI") ?: ""

        loadPetugasData()

        adapterSpin = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayInformasi)
        b.spJenis1.adapter = adapterSpin

        b.btnKembaliInformasi1.setOnClickListener(this)
        b.btnFotoInformasi1.setOnClickListener(this)
        b.btnSimpanInformasi1.setOnClickListener(this)
    }

    private fun isFormValid(): Boolean {
        return (
                !b.edJudulInformasi1.text.toString().isEmpty() &&
                !b.edDeskripsiInformasi1.text.toString().isEmpty()
                )
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnFotoInformasi1 -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
            R.id.btnSimpanInformasi1 -> {
                if (isFormValid()){
                    val id = b.edIdInformasi1.text.toString()
                    val judul = b.edJudulInformasi1.text.toString()
                    val jenis = adapterSpin.getItem(b.spJenis1.selectedItemPosition)!!
                    val deskripsi = b.edDeskripsiInformasi1.text.toString()
                    val tanggal = b.edTanggalInformasi1.text.toString()
                    val image = b.edImageUrl1.text.toString()

                    val informasi = Informasi(id, judul, jenis, tanggal, deskripsi, image)

                    if (imageUri != null) {
                        val imageRef = storage.child("informasi/$id.jpg")
                        val uploadTask = imageRef.putFile(imageUri!!)

                        uploadTask.addOnSuccessListener { _ ->
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                informasi.imageUrl = uri.toString()
                                saveUpdatedPetugas(informasi)
                            }
                        }.addOnFailureListener { exception ->
                            showErrorDialog("Terjadi kesalahan saat mengunggah gambar: ${exception.message}")
                        }
                    } else {
                        saveUpdatedPetugas(informasi)
                    }
                } else {
                    Toast.makeText(this, "Semua form harus diisi !!!", Toast.LENGTH_LONG).show()
                }
            }
            R.id.btnKembaliInformasi1 -> {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            b.tvFotoInformasi1.text = "Foto telah diunggah"
        }
    }

    private fun loadPetugasData() {
        db.child(informasiId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val informasi = dataSnapshot.getValue(Informasi::class.java)
                informasi?.let {
                    with(b) {
                        edIdInformasi1.setText(it.id)
                        edJudulInformasi1.setText(it.judul)
                        edDeskripsiInformasi1.setText(it.deskripsi)
                        edTanggalInformasi1.setText(it.tanggal)
                        edImageUrl1.setText(it.imageUrl)

                        val position = arrayInformasi.indexOf(it.jenis)
                        b.spJenis1.setSelection(position)
                    }
                }
            }
        }
    }

    private fun saveUpdatedPetugas(informasi: Informasi) {
        db.child(informasiId).setValue(informasi)
        showSuccessDialog()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("BERHASIL")
            setMessage("Data informasi berhasil diperbarui !")
            setPositiveButton("Ya") { _, _ -> finish() }
        }.create().show()
    }

    private fun showErrorDialog(errorMessage: String) {
        AlertDialog.Builder(this).apply {
            setTitle("ERROR")
            setMessage(errorMessage)
            setPositiveButton("Ya") { _, _ -> }
        }.create().show()
    }
}