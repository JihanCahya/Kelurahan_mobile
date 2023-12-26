package com.polinema.uas.sipkburengan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polinema.uas.sipkburengan.databinding.ActivityHistorySuratBinding
import com.polinema.uas.sipkburengan.databinding.ActivityValidasiAdminBinding

class HistorySuratActivity : Fragment() {
    private lateinit var b: ActivityHistorySuratBinding
    lateinit var thisParent: MainActivity
    lateinit var v: View
    private lateinit var db: DatabaseReference
    private lateinit var db_user: DatabaseReference
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        b = ActivityHistorySuratBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        db_user = FirebaseDatabase.getInstance().getReference("Data_user")
        val adapter = ValidasiAdapter(requireContext(), ArrayList())

        b.lvhistory.adapter = adapter
        b.lvhistory.setOnItemClickListener { parent, view, position, id ->
            val selectedSurat = adapter.getItem(position)
            if (selectedSurat != null){
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Detail Surat")
                builder.setMessage("Nama : ${selectedSurat.nama_pengaju}\nSurat : ${selectedSurat.surat}\nJenis Surat : ${selectedSurat.jenisSurat}\nTanggal Pengajuan : ${selectedSurat.tanggalPengajuan}\nStatus : ${selectedSurat.status}")
                val status = selectedSurat.status
                if (status == "Belum terpenuhi"){
                    builder.setPositiveButton("Edit") { dialog, _ ->
                        val intent = Intent(requireContext(), EditSuratActivity::class.java)
                        intent.putExtra("ID_SURAT", selectedSurat.id)
                        startActivity(intent)
                    }
                }
                builder.show()
            }
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        fetchHistoryData()
    }

    private fun fetchHistoryData() {
        val adapter = b.lvhistory.adapter as HistoryAdapter
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val historyData = ArrayList<History>()
                for (childSnapshot in dataSnapshot.children) {
                    val history = childSnapshot.getValue(History::class.java)
                    if (history != null) {
                        historyData .add(history)
                    }
                }
                adapter.clear()
                adapter.addAll(historyData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// =======================================================
data class History(
    val id: String,
    val nama_pengaju: String,
    val surat: String,
    val jenisSurat: String,
    val status: String,
    var tanggalPengajuan: String,
    var keterangan: String,
    var imageUrlAkta: String,
    var imageUrlKK: String,
    var imageUrlKTP: String,
    var imageUrlPengantarRT: String
) {
    constructor() : this( "","", "", "","", "", "", "", "","","")
}

// ========================================================
class HistoryAdapter(context: Context, data: List<History>) :
    ArrayAdapter<History>(context, R.layout.item_validasi, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_validasi, parent, false)

        val validasi = getItem(position)
        val namaTV: TextView = itemView.findViewById(R.id.tvNamaValidasi)
        val suratTV: TextView = itemView.findViewById(R.id.tvSuratValidasi)
        val jenisSuratTV: TextView = itemView.findViewById(R.id.tvJenisSuratValidasi)
        val tanggalTV: TextView = itemView.findViewById(R.id.tvTanggalValidasi)
        val KeteranganStatus: TextView = itemView.findViewById(R.id.ketStatus)
        val status: Button = itemView.findViewById(R.id.btnStatusValidasi)

        if (validasi != null) {
            if(validasi.status == "Arsip"){
                namaTV.visibility = View.GONE
                suratTV.visibility = View.GONE
                jenisSuratTV.visibility = View.GONE
                tanggalTV.visibility = View.GONE
                KeteranganStatus.visibility = View.GONE
                status.visibility = View.GONE
            } else {
                namaTV.text = "Nama : ${validasi.nama_pengaju}"
                suratTV.text = "Surat : ${validasi.surat}"
                jenisSuratTV.text = "Jenis : ${validasi.jenisSurat}"
                tanggalTV.text = "Tanggal diajukan : ${validasi.tanggalPengajuan}"
                status.text = "${validasi.status}"
                if (validasi.status == "Belum dicek") {
                    status.setBackgroundColor(Color.RED)
                    status.setTextColor(Color.WHITE)
                } else if (validasi.status == "Belum terpenuhi") {
                    status.setBackgroundColor(Color.YELLOW)
                    status.setTextColor(Color.BLACK)
                } else if (validasi.status == "Sudah diperbarui") {
                    status.setBackgroundColor(Color.BLACK)
                    status.setTextColor(Color.WHITE)
                } else if (validasi.status == "Terpenuhi") {
                    status.setBackgroundColor(Color.GREEN)
                    status.setTextColor(Color.BLACK)
                } else if (validasi.status == "Sudah diambil") {
                    status.setBackgroundColor(Color.BLUE)
                    status.setTextColor(Color.WHITE)
                }
            }
        }

        return itemView
    }
}