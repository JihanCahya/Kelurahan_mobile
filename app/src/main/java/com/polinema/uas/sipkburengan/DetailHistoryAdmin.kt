package com.polinema.uas.sipkburengan

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.polinema.uas.sipkburengan.databinding.ActivityDetailHistoryAdminBinding

class DetailHistoryAdmin : AppCompatActivity() {

    private lateinit var b : ActivityDetailHistoryAdminBinding
    private lateinit var suratId: String
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailHistoryAdminBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        suratId = intent.getStringExtra("ID_SURAT") ?: ""

        b.btnKembaliArsip1.setOnClickListener {
            finish()
        }

        loadDataArsip()
    }

    private fun loadDataArsip() {
        db.child(suratId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val arsip = dataSnapshot.getValue(Arsip::class.java)
                arsip?.let {
                    with(b) {
                        Glide.with(this@DetailHistoryAdmin)
                            .load(arsip.imageArsip)
                            .into(imageView2)
                        b.textView6.setText(it.surat)
                        b.textView7.setText(it.jenisSurat)
                        val link = it.imageArsip
                        b.imageView2.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}