
package com.polinema.uas.sipkburengan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.polinema.uas.sipkburengan.databinding.ActivityDetailBantuanAcitivityBinding

class DetailBantuanAcitivity : AppCompatActivity() {

    private lateinit var b : ActivityDetailBantuanAcitivityBinding
    private lateinit var db: DatabaseReference
    private lateinit var bantuanId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailBantuanAcitivityBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("informasi")
        bantuanId = intent.getStringExtra("ID_BANTUAN") ?: ""

        loadBantuanData()
    }

    private fun loadBantuanData() {
        db.child(bantuanId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val bantuan = dataSnapshot.getValue(Bantuan::class.java)
                bantuan?.let {
                    with(b) {
                        tvJudulBantuanDetail.setText(it.judul)
                        tvTglBantuanDetail.setText(it.tanggal)
                        tvIsiBantuan.setText(it.deskripsi)

                        Glide.with(this@DetailBantuanAcitivity)
                            .load(it.imageUrl)
                            .into(imageDetailBantuan)
                    }
                }
            }
        }
    }
}