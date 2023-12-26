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
import com.polinema.uas.sipkburengan.databinding.ActivityHistoryAdminBinding

class HistoryAdminActivity : Fragment() {

    lateinit var thisParent: DashboardAdminActivity
    private lateinit var b : ActivityHistoryAdminBinding
    lateinit var v: View
    private lateinit var db: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as DashboardAdminActivity
        b = ActivityHistoryAdminBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("Pengajuan")
        val adapter = ArsipAdapter(requireContext(), ArrayList())
        b.lsHsitoryAdmin.adapter = adapter

        b.lsHsitoryAdmin.setOnItemClickListener { parent, view, position, id ->
            val selectedSurat = adapter.getItem(position)
            if (selectedSurat != null){
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Detail Surat")
                builder.setMessage("Nama : ${selectedSurat.nama_pengaju}\nSurat : ${selectedSurat.surat}\nJenis Surat : ${selectedSurat.jenisSurat}\nTanggal Pengajuan : ${selectedSurat.tanggalPengajuan}\nTanggal Selesai : ${selectedSurat.tanggalSelesai}")
                builder.setPositiveButton("Lihat surat") { dialog, _ ->
                    val intent = Intent(requireContext(), DetailHistoryAdmin::class.java)
                    intent.putExtra("ID_SURAT", selectedSurat.id)
                    startActivity(intent)
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
        val adapter = b.lsHsitoryAdmin.adapter as ArsipAdapter
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val arsipData = ArrayList<Arsip>()
                for (childSnapshot in dataSnapshot.children) {
                    val arsip = childSnapshot.getValue(Arsip::class.java)
                    if (arsip != null && arsip.status == "Arsip") {
                        arsipData.add(arsip)
                    }
                }
                adapter.clear()
                adapter.addAll(arsipData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// ========================================================
class ArsipAdapter(context: Context, data: List<Arsip>) :
    ArrayAdapter<Arsip>(context, R.layout.item_history_admin, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_history_admin, parent, false)

        val arsip = getItem(position)
        val namaTV: TextView = itemView.findViewById(R.id.tvNamaHistory)
        val suratTV: TextView = itemView.findViewById(R.id.tvNamaSuratAdmin)
        val tanggalPengajuanTV: TextView = itemView.findViewById(R.id.tvTglPengajuanAdmin)
        val tanggalSelesaiTV: TextView = itemView.findViewById(R.id.tvTglSelesaiAdmin)

        if (arsip != null) {
            namaTV.text = "Nama : ${arsip.nama_pengaju}"
            suratTV.text = "Surat : ${arsip.surat}"
            tanggalPengajuanTV.text = "Tanggal pengajuan : ${arsip.tanggalPengajuan}"
            tanggalSelesaiTV.text = "Tanggal selesai : ${arsip.tanggalSelesai}"

        }

        return itemView
    }
}