package com.polinema.uas.sipkburengan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.polinema.uas.sipkburengan.databinding.ActivityDetailInformasiBinding

class DetailInformasiActivity : AppCompatActivity() {

    private lateinit var b : ActivityDetailInformasiBinding
    private lateinit var db: DatabaseReference
    private lateinit var beritaId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailInformasiBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("informasi")
        beritaId = intent.getStringExtra("ID_BERITA") ?: ""

        loadBeritaData()
    }

    private fun loadBeritaData() {
        db.child(beritaId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val berita = dataSnapshot.getValue(Berita::class.java)
                berita?.let {
                    with(b) {
                        tvJudulInformasiDetail.setText(it.judul)
                        tvTglInformasiDetail.setText(it.tanggal)
                        tvIsiInformasi.setText(it.deskripsi)

                        Glide.with(this@DetailInformasiActivity)
                            .load(it.imageUrl)
                            .into(imageDetailInformasi)
                    }
                }
            }
        }
    }
}