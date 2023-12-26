package com.polinema.uas.sipkburengan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.polinema.uas.sipkburengan.databinding.ActivityDetailSuratKtpactivityBinding

class DetailSuratKTPActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityDetailSuratKtpactivityBinding
    private lateinit var db: DatabaseReference
    private lateinit var suratId: String
    lateinit var adapterSpin : ArrayAdapter<String>
    val arrayStatusKTP = arrayOf("Belum dicek", "Belum terpenuhi", "Terpenuhi", "Sudah diambil")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetailSuratKtpactivityBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        suratId = intent.getStringExtra("ID_SURAT") ?: ""

//        loadDetailKTP()

        adapterSpin = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayStatusKTP)
        b.spStatusKTP.adapter = adapterSpin

        b.btnKembaliKTP.setOnClickListener(this)
        b.btnSimpanKTP.setOnClickListener(this)

        b.spStatusKTP.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val jenis = adapterSpin.getItem(position)
                if (jenis == "Belum terpenuhi"){
                    b.textInputLayout31.visibility = View.VISIBLE
                } else {
                    b.textInputLayout31.visibility = View.INVISIBLE
                }
            }
        }

        loadDetailKTP()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnKembaliKTP -> {
                finish()
            }
            R.id.btnSimpanKTP -> {

            }
        }
    }

    private fun loadDetailKTP() {
        db.child(suratId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val validasi = dataSnapshot.getValue(Validasi::class.java)
                validasi?.let {
                    with(b) {

                        Glide.with(this@DetailSuratKTPActivity)
                            .load(validasi.imageUrlAkta)
                            .into(imFotoAkta)

                        Glide.with(this@DetailSuratKTPActivity)
                            .load(validasi.imageUrlKTP)
                            .into(imFotoKTP)

                        Glide.with(this@DetailSuratKTPActivity)
                            .load(validasi.imageUrlKK)
                            .into(imFotoKK)

                        Glide.with(this@DetailSuratKTPActivity)
                            .load(validasi.imageUrlPengantarRT)
                            .into(imFotoRT)

                        val position = arrayStatusKTP.indexOf(it.status)
                        b.spStatusKTP.setSelection(position)
                    }
                }
            }
        }
    }
}