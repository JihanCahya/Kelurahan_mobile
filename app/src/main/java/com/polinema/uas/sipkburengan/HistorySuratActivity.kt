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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polinema.uas.sipkburengan.databinding.ActivityHistorySuratBinding

class HistorySuratActivity : Fragment() {
    private lateinit var b: ActivityHistorySuratBinding
    lateinit var thisParent: MainActivity
    lateinit var v: View
    private lateinit var db: DatabaseReference
    private lateinit var db_user: DatabaseReference

    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        b = ActivityHistorySuratBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        db_user = FirebaseDatabase.getInstance().getReference("Data_user")
        val adapter = HistoryAdapter(requireContext(), ArrayList())

        b.lvhistory.adapter = adapter
        b.lvhistory.setOnItemClickListener { parent, view, position, id ->
            val selectedSurat = adapter.getItem(position)
            if (selectedSurat != null){
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Detail Surat")
                if (selectedSurat.status != "Belum dicek"){
                    if (selectedSurat.status == "Terpenuhi") {
                        builder.setMessage("Nama : ${selectedSurat.nama_pengaju}\nSurat : ${selectedSurat.surat}\nJenis Surat : ${selectedSurat.jenisSurat}\nTanggal Pengajuan : ${selectedSurat.tanggalPengajuan}\nStatus : ${selectedSurat.status}\nKeterangan : ${selectedSurat.keterangan}\nTanggal Selesai : ${selectedSurat.tanggalSelesai}")
                    } else {
                        builder.setMessage("Nama : ${selectedSurat.nama_pengaju}\nSurat : ${selectedSurat.surat}\nJenis Surat : ${selectedSurat.jenisSurat}\nTanggal Pengajuan : ${selectedSurat.tanggalPengajuan}\nStatus : ${selectedSurat.status}\nKeterangan : ${selectedSurat.keterangan}")
                    }
                }else {
                    builder.setMessage("Nama : ${selectedSurat.nama_pengaju}\nSurat : ${selectedSurat.surat}\nJenis Surat : ${selectedSurat.jenisSurat}\nTanggal Pengajuan : ${selectedSurat.tanggalPengajuan}\nStatus : ${selectedSurat.status}")
                }
                val status = selectedSurat.status
                val nama_surat = selectedSurat.surat
                if (status == "Belum terpenuhi"){
                    if(nama_surat == "Surat Keterangan") {
                        builder.setPositiveButton("Edit") { dialog, _ ->
                            val intent = Intent(requireContext(), EditSuratKeteranganActivity::class.java)
                            intent.putExtra("ID_SURAT", selectedSurat.id)
                            startActivity(intent)
                        }
                    } else if(nama_surat == "Surat Pengajuan KK") {
                        builder.setPositiveButton("Edit") { dialog, _ ->
                            val intent = Intent(requireContext(), EditSuratKKActivity::class.java)
                            intent.putExtra("ID_SURAT", selectedSurat.id)
                            startActivity(intent)
                        }
                    } else if(nama_surat == "Surat Pengajuan KTP") {
                        builder.setPositiveButton("Edit") { dialog, _ ->
                            val intent = Intent(requireContext(), EditSuratKtpActivity::class.java)
                            intent.putExtra("ID_SURAT", selectedSurat.id)
                            startActivity(intent)
                        }
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
        db.orderByChild("id_pengaju").equalTo(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val historyData = ArrayList<History>()
                for (childSnapshot in dataSnapshot.children) {
                    val history = childSnapshot.getValue(History::class.java)
                    if (history != null) {
                        historyData.add(history)
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
    val id_pengaju: String,
    val nama_pengaju: String,
    val surat: String,
    val jenisSurat: String,
    var status: String,
    var tanggalPengajuan: String,
    var keterangan: String,
    var imageUrlAkta: String,
    var imageUrlKK: String,
    var imageUrlKTP: String,
    var imageUrlPengantarRT: String,
    var tanggalSelesai: String
) {
    constructor() : this("", "","", "", "", "", "", "", "", "", "", "", "")
}


// ========================================================
class HistoryAdapter(context: Context, data: List<History>) :
    ArrayAdapter<History>(context, R.layout.item_history_surat, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_history_surat, parent, false)

        val history = getItem(position)
        val suratTV: TextView = itemView.findViewById(R.id.tvNamaSurat)
        val jenisSuratTV: TextView = itemView.findViewById(R.id.tvJenisSurat)
        val tanggalTV: TextView = itemView.findViewById(R.id.tvTglPengajuan)
        val KeteranganStatus: TextView = itemView.findViewById(R.id.KetStatusSurat)
        val status: Button = itemView.findViewById(R.id.btnStatusSurat)

        if (history != null) {
            if(history.status == "Arsip"){
                suratTV.visibility = View.GONE
                jenisSuratTV.visibility = View.GONE
                tanggalTV.visibility = View.GONE
                KeteranganStatus.visibility = View.GONE
                status.visibility = View.GONE
            } else {
                suratTV.text = "Surat : ${history.surat}"
                jenisSuratTV.text = "Jenis : ${history.jenisSurat}"
                tanggalTV.text = "Tanggal diajukan : ${history.tanggalPengajuan}"
                status.text = "${history.status}"
                if (history.status == "Belum dicek") {
                    status.setBackgroundColor(Color.RED)
                    status.setTextColor(Color.WHITE)
                } else if (history.status == "Belum terpenuhi") {
                    status.setBackgroundColor(Color.YELLOW)
                    status.setTextColor(Color.BLACK)
                } else if (history.status == "Sudah diperbarui") {
                    status.setBackgroundColor(Color.BLACK)
                    status.setTextColor(Color.WHITE)
                } else if (history.status == "Terpenuhi") {
                    status.setBackgroundColor(Color.GREEN)
                    status.setTextColor(Color.BLACK)
                } else if (history.status == "Sudah diambil") {
                    status.setBackgroundColor(Color.BLUE)
                    status.setTextColor(Color.WHITE)
                }
            }
        }

        return itemView
    }
}