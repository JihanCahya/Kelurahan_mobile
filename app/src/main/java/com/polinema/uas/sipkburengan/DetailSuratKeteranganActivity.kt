package com.polinema.uas.sipkburengan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.polinema.uas.sipkburengan.databinding.ActivityDetailSuratKeteranganBinding

class DetailSuratKeteranganActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityDetailSuratKeteranganBinding
    private lateinit var db: DatabaseReference
    private lateinit var suratId: String
    lateinit var adapterSpin : ArrayAdapter<String>
    val arrayStatusKeterangan = arrayOf("Belum dicek", "Belum terpenuhi", "Terpenuhi", "Sudah diambil")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailSuratKeteranganBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        suratId = intent.getStringExtra("ID_SURAT") ?: ""

        adapterSpin = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayStatusKeterangan)
        b.spStatusKeterangan.adapter = adapterSpin

        b.btnKembaliKeterangan.setOnClickListener(this)
        b.btnSimpanKeterangan.setOnClickListener(this)
        b.spStatusKeterangan.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val jenis = adapterSpin.getItem(position)
                if (jenis == "Belum terpenuhi"){
                    b.textInputLayout33.visibility = View.VISIBLE
                } else {
                    b.textInputLayout33.visibility = View.INVISIBLE
                }
            }
        }
        loadDetailKeterangan()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnKembaliKeterangan -> {
                finish()
            }
            R.id.btnSimpanKeterangan -> {

            }
        }
    }

    private fun loadDetailKeterangan() {
        db.child(suratId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val validasi = dataSnapshot.getValue(Validasi::class.java)
                validasi?.let {
                    with(b) {

                        Glide.with(this@DetailSuratKeteranganActivity)
                            .load(validasi.imageUrlKTP)
                            .into(imFotoKTP2)

                        Glide.with(this@DetailSuratKeteranganActivity)
                            .load(validasi.imageUrlPengantarRT)
                            .into(imPengantar3)

                        val position = arrayStatusKeterangan.indexOf(it.status)
                        b.spStatusKeterangan.setSelection(position)
                    }
                }
            }
        }
    }
}