package com.polinema.uas.sipkburengan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.polinema.uas.sipkburengan.databinding.ActivityDetailSuratKkactivityBinding

class DetailSuratKKActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityDetailSuratKkactivityBinding
    private lateinit var db: DatabaseReference
    private lateinit var suratId: String
    lateinit var adapterSpin : ArrayAdapter<String>
    val arrayStatusKK = arrayOf("Belum dicek", "Belum terpenuhi", "Terpenuhi", "Sudah diambil")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailSuratKkactivityBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        suratId = intent.getStringExtra("ID_SURAT") ?: ""

        adapterSpin = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayStatusKK)
        b.spStatusKK.adapter = adapterSpin

        b.btnKembaliKK1.setOnClickListener(this)
        b.btnSimpanKK1.setOnClickListener(this)

        b.spStatusKK.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val jenis = adapterSpin.getItem(position)
                if (jenis == "Belum terpenuhi"){
                    b.textInputLayout32.visibility = View.VISIBLE
                } else {
                    b.textInputLayout32.visibility = View.INVISIBLE
                }
            }
        }

        loadDetailKK()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnKembaliKK1 -> {
                finish()
            }
            R.id.btnSimpanKK1 -> {

            }
        }
    }

    private fun loadDetailKK() {
        db.child(suratId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val validasi = dataSnapshot.getValue(Validasi::class.java)
                validasi?.let {
                    with(b) {

                        Glide.with(this@DetailSuratKKActivity)
                            .load(validasi.imageUrlKTP)
                            .into(imFotoKTP1)

                        Glide.with(this@DetailSuratKKActivity)
                            .load(validasi.imageUrlPengantarRT)
                            .into(imPengantar1)

                        val position = arrayStatusKK.indexOf(it.status)
                        b.spStatusKK.setSelection(position)
                    }
                }
            }
        }
    }
}