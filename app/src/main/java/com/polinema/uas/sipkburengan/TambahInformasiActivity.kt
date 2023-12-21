package com.polinema.uas.sipkburengan

import android.content.Intent
import android.icu.text.IDNA.Info
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.uas.sipkburengan.databinding.ActivityTambahInformasiBinding
import java.util.Calendar

class TambahInformasiActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityTambahInformasiBinding
    private lateinit var db: DatabaseReference
    private lateinit var storage: StorageReference
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    val arrayInformasi = arrayOf("Berita kelurahan", "Informasi bantuan")
    lateinit var adapterSpin : ArrayAdapter<String>
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val date = "$day-$month-$year"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTambahInformasiBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("informasi")
        storage = FirebaseStorage.getInstance().reference

        adapterSpin = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayInformasi)
        b.spJenis.adapter = adapterSpin

        b.btnKembaliInformasi.setOnClickListener(this)
        b.btnFotoInformasi.setOnClickListener(this)
        b.btnTambahInformasi.setOnClickListener(this)
    }

    private fun isFormValid(): Boolean {
        return (
                !b.edJudulInformasi.text.toString().isEmpty() &&
                !b.edDeskripsiInformasi.text.toString().isEmpty()
                )
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnFotoInformasi -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
            R.id.btnTambahInformasi -> {
                if (isFormValid()){
                    val judul = b.edJudulInformasi.text.toString()
                    val jenis = adapterSpin.getItem(b.spJenis.selectedItemPosition)!!
                    val deskripsi = b.edDeskripsiInformasi.text.toString()
                    val tanggal = date

                    val newRef = db.push()
                    val uniqueKey = newRef.key

                    if (imageUri != null){
                        val imageRef = storage.child("informasi/$uniqueKey.jpg")
                        val uploadTask = imageRef.putFile(imageUri!!)

                        uploadTask.addOnSuccessListener{ taskSnapshot ->
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()

                                val informasi = Informasi(uniqueKey.toString(), judul, jenis, tanggal, deskripsi, imageUrl)
                                val informasiRef = db.child(uniqueKey.toString())
                                informasiRef.setValue(informasi)

                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("BERHASIL")
                                builder.setMessage("Data $jenis berhasil diunggah !")
                                builder.setPositiveButton("Ya") { _, _ ->
                                    finish()
                                }
                                val dialog = builder.create()
                                dialog.show()
                            }
                        }.addOnFailureListener{ exception ->
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("ERROR")
                            builder.setMessage("Terjadi kesalahan saat mengunggah gambar: ${exception.message}")
                            builder.setPositiveButton("Ya") { _, _ -> }
                            val dialog = builder.create()
                            dialog.show()
                        }
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("ERROR")
                        builder.setMessage("Pilih gambar terlebih dahulu")
                        builder.setPositiveButton("Ya") { _, _ -> }
                        val dialog = builder.create()
                        dialog.show()
                    }
                } else {
                    Toast.makeText(this, "Semua form harus diisi !!!", Toast.LENGTH_LONG).show()
                }
            }
            R.id.btnKembaliInformasi -> {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            b.tvFotoInformasi.text = imageUri.toString()
        }
    }
}