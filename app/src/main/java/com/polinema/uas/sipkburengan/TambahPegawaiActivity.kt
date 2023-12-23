package com.polinema.uas.sipkburengan

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.uas.sipkburengan.databinding.ActivityTambahPegawaiBinding

class TambahPegawaiActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityTambahPegawaiBinding
    private lateinit var db: DatabaseReference
    private lateinit var db_jabatan: DatabaseReference
    private lateinit var storage: StorageReference
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    val arrayPegawai = mutableListOf<String>()
    lateinit var adapterSpin : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTambahPegawaiBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("pegawai")
        storage = FirebaseStorage.getInstance().reference

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
                        this@TambahPegawaiActivity,
                        android.R.layout.simple_list_item_1,
                        arrayPegawai
                    )

                    b.spJabatan.adapter = adapterSpin
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@TambahPegawaiActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })

        b.btnKembaliPegawai.setOnClickListener(this)
        b.btnFotoPegawai.setOnClickListener(this)
        b.btnSimpanPegawai.setOnClickListener(this)
    }

    private fun isFormValid(): Boolean {
        return (
                !b.edNamaPegawai.text.toString().isEmpty() &&
                !b.edNipPegawai.text.toString().isEmpty() &&
                !b.edAlamatPegawai.text.toString().isEmpty()
                )
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnFotoPegawai -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
            R.id.btnSimpanPegawai -> {
                if (isFormValid()){
                    val nama = b.edNamaPegawai.text.toString()
                    val nip = b.edNipPegawai.text.toString()
                    val jabatan = adapterSpin.getItem(b.spJabatan.selectedItemPosition)!!
                    val alamat = b.edAlamatPegawai.text.toString()

                    val newRef = db.push()
                    val uniqueKey = newRef.key

                    if (imageUri != null){
                        b.progressBar3.visibility = View.VISIBLE
                        val imageRef = storage.child("pegawai/$uniqueKey.jpg")
                        val uploadTask = imageRef.putFile(imageUri!!)

                        uploadTask.addOnSuccessListener{ taskSnapshot ->
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()

                                val pegawai = Pegawai(uniqueKey.toString(), nama, nip, jabatan, alamat, imageUrl)
                                val pegawaiRef = db.child(uniqueKey.toString())
                                pegawaiRef.setValue(pegawai)

                                b.progressBar3.visibility = View.GONE
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("BERHASIL")
                                builder.setMessage("Data pegawai berhasil diunggah !")
                                builder.setPositiveButton("Ya") { _, _ ->
                                    finish()
                                }
                                val dialog = builder.create()
                                dialog.show()
                            }
                        }.addOnFailureListener{ exception ->
                            b.progressBar3.visibility = View.GONE
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
                } else {
                    Toast.makeText(this, "Semua form harus diisi !!!", Toast.LENGTH_LONG).show()
                }
            }
            R.id.btnKembaliPegawai -> {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            b.tvNamaFotoPegawai.text = "Foto telah diunggah"
        }
    }
}