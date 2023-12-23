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
import com.polinema.uas.sipkburengan.databinding.ActivityEditPegawaiBinding

class EditPegawaiActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityEditPegawaiBinding
    private lateinit var db: DatabaseReference
    private lateinit var db_jabatan: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var pegawaiId: String
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 2
    val arrayPegawai = mutableListOf<String>()
    lateinit var adapterSpin : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditPegawaiBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("pegawai")
        storage = FirebaseStorage.getInstance().reference

        pegawaiId = intent.getStringExtra("ID_PEGAWAI") ?: ""

        db_jabatan = FirebaseDatabase.getInstance().getReference("jabatan")
        db_jabatan.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (jabatanSnapshot in dataSnapshot.children) {
                        val jabatan = jabatanSnapshot.child("jabatan").getValue(String::class.java)
                        jabatan?.let {
                            arrayPegawai.add(it)
                        }
                    }
                    adapterSpin = ArrayAdapter(
                        this@EditPegawaiActivity,
                        android.R.layout.simple_list_item_1,
                        arrayPegawai
                    )

                    b.spJabatan1.adapter = adapterSpin
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@EditPegawaiActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })

        loadPegawaiData()

        b.btnKembaliPegawai1.setOnClickListener(this)
        b.btnFotoPegawai1.setOnClickListener(this)
        b.btnSimpanPegawai1.setOnClickListener(this)
    }

    private fun isFormValid(): Boolean {
        return (
                !b.edNamaPegawai1.text.toString().isEmpty() &&
                !b.edNipPegawai1.text.toString().isEmpty() &&
                !b.edAlamatPegawai1.text.toString().isEmpty()
                )
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnFotoPegawai1 -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
            R.id.btnSimpanPegawai1 -> {
                if (isFormValid()){
                    b.progressBar4.visibility = View.VISIBLE
                    val id = b.edIdPegawai1.text.toString()
                    val nama = b.edNamaPegawai1.text.toString()
                    val nip = b.edNipPegawai1.text.toString()
                    val jabatan = adapterSpin.getItem(b.spJabatan1.selectedItemPosition)!!
                    val alamat = b.edAlamatPegawai1.text.toString()
                    val image = b.edImagePegawai1.text.toString()

                    val pegawai = Pegawai(id, nama, nip, jabatan, alamat, image)

                    if (imageUri != null) {
                        val imageRef = storage.child("pegawai/$id.jpg")
                        val uploadTask = imageRef.putFile(imageUri!!)

                        uploadTask.addOnSuccessListener { _ ->
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                pegawai.imageUrl = uri.toString()
                                saveUpdatedPegawai(pegawai)
                            }
                        }.addOnFailureListener { exception ->
                            showErrorDialog("Terjadi kesalahan saat mengunggah gambar: ${exception.message}")
                        }
                    } else {
                        saveUpdatedPegawai(pegawai)
                    }
                } else {
                    Toast.makeText(this, "Semua form harus diisi !!!", Toast.LENGTH_LONG).show()
                }
            }
            R.id.btnKembaliPegawai1 -> {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            b.tvNamaFotoPegawai1.text = "Foto telah diunggah"
        }
    }

    private fun loadPegawaiData() {
        db.child(pegawaiId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val pegawai = dataSnapshot.getValue(Pegawai::class.java)
                pegawai?.let {
                    with(b) {
                        edIdPegawai1.setText(it.id)
                        edNamaPegawai1.setText(it.nama)
                        edNipPegawai1.setText(it.nip)
                        edAlamatPegawai1.setText(it.alamat)
                        edImagePegawai1.setText(it.imageUrl)

                        val position = arrayPegawai.indexOf(it.jabatan)
                        b.spJabatan1.setSelection(position)
                    }
                }
            }
        }
    }

    private fun saveUpdatedPegawai(pegawai: Pegawai) {
        db.child(pegawaiId).setValue(pegawai)
        showSuccessDialog()
    }

    private fun showSuccessDialog() {
        b.progressBar4.visibility = View.GONE
        AlertDialog.Builder(this).apply {
            setTitle("BERHASIL")
            setMessage("Data informasi berhasil diperbarui !")
            setPositiveButton("Ya") { _, _ -> finish() }
        }.create().show()
    }

    private fun showErrorDialog(errorMessage: String) {
        b.progressBar4.visibility = View.GONE
        AlertDialog.Builder(this).apply {
            setTitle("ERROR")
            setMessage(errorMessage)
            setPositiveButton("Ya") { _, _ -> }
        }.create().show()
    }
}