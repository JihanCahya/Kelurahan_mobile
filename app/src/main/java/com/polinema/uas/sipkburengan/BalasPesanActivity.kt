package com.polinema.uas.sipkburengan

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.polinema.uas.sipkburengan.databinding.ActivityBalasPesanBinding
import java.util.Calendar

class BalasPesanActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityBalasPesanBinding
    private lateinit var db: DatabaseReference
    private lateinit var pesanId: String
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val date = "$day-$month-$year"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityBalasPesanBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("kritik_saran")

        pesanId = intent.getStringExtra("ID_PESAN") ?: ""

        loadPesanData()

        b.btnKembaliPesan.setOnClickListener(this)
        b.btnSimpanPesan1.setOnClickListener(this)
    }

    private fun isFormValid(): Boolean {
        return (
                !b.edIdPesan1.text.toString().isEmpty() &&
                !b.edNamaPesan1.text.toString().isEmpty() &&
                !b.edBidangPesan1.text.toString().isEmpty() &&
                !b.edPesan1.text.toString().isEmpty() &&
                !b.edWaktuKirimPesan1.text.toString().isEmpty() &&
                !b.edBalasPesan1.text.toString().isEmpty()
                )
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnKembaliPesan -> {
                finish()
            }
            R.id.btnSimpanPesan1 -> {
                if (isFormValid()){
                    b.progressBar7.visibility = View.VISIBLE
                    val idP = b.edIdPesan1.text.toString()
                    val namaP = b.edNamaPesan1.text.toString()
                    val bidangP = b.edBidangPesan1.text.toString()
                    val pesanP = b.edPesan1.text.toString()
                    val waktuKP = b.edWaktuKirimPesan1.text.toString()
                    val balasP = b.edBalasPesan1.text.toString()

                    val pesan = Pesan(idP, namaP, bidangP, pesanP, waktuKP, "Sudah dibalas", balasP, date)
                    saveUpdatedPesan(pesan)
                } else {
                    Toast.makeText(this, "Semua form harus diisi !!!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadPesanData() {
        db.child(pesanId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val pesan = dataSnapshot.getValue(Pesan::class.java)
                pesan?.let {
                    with(b) {
                        edIdPesan1.setText(it.id)
                        edNamaPesan1.setText(it.nama)
                        edBidangPesan1.setText(it.bidang)
                        edPesan1.setText(it.pesan)
                        edWaktuKirimPesan1.setText(it.waktu_dikirim)
                    }
                }
            }
        }
    }

    private fun saveUpdatedPesan(pesan: Pesan) {
        db.child(pesanId).setValue(pesan)
        b.progressBar7.visibility = View.GONE
        AlertDialog.Builder(this).apply {
            setTitle("BERHASIL")
            setMessage("Data informasi berhasil diperbarui !")
            setPositiveButton("Ya") { _, _ -> finish() }
        }.create().show()
    }
}