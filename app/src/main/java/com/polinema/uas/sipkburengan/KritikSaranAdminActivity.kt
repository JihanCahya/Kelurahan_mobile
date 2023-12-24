package com.polinema.uas.sipkburengan

import android.content.Context
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
import com.polinema.uas.sipkburengan.databinding.ActivityKritikSaranAdminBinding

class KritikSaranAdminActivity : Fragment() {

    lateinit var thisParent: DashboardAdminActivity
    private lateinit var b: ActivityKritikSaranAdminBinding
    lateinit var v: View
    private lateinit var db: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as DashboardAdminActivity
        b = ActivityKritikSaranAdminBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("kritik_saran")
        val adapter = PesanAdapter(requireContext(), ArrayList())

        b.lvKritikSaran.adapter = adapter

        b.lvKritikSaran.setOnItemClickListener { parent, view, position, id ->
            val selectedPesan = adapter.getItem(position)
            if (selectedPesan != null){
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Detail Kritik & Saran")
                builder.setMessage("Nama : ${selectedPesan.nama}\nBidang : ${selectedPesan.bidang}\nPesan : ${selectedPesan.pesan}\nWaktu dikirim : ${selectedPesan.waktu_dikirim}\nStatus : ${selectedPesan.status}\nBalasan : ${selectedPesan.balasan}\nWaktu dibalas : ${selectedPesan.waktu_dibalas}")
                val status = selectedPesan.status
                if (status == "Belum dicek"){
                    builder.setPositiveButton("Baca") { dialog, _ ->
                        Toast.makeText(requireContext(), "Baca", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("Balas") { dialog, _ ->
                        Toast.makeText(requireContext(), "Balas", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    builder.setNeutralButton("Detail") { dialog, _ ->
                        Toast.makeText(requireContext(), "Detail", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    builder.show()
                } else if (status == "Sudah dibaca") {
                    builder.setPositiveButton("Balas") { dialog, _ ->
                        Toast.makeText(requireContext(), "Balas", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("Detail") { dialog, _ ->
                        Toast.makeText(requireContext(), "Detail", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    builder.show()
                } else if (status == "Sudah dibalas") {
                    builder.setPositiveButton("Detail") { dialog, _ ->
                        Toast.makeText(requireContext(), "Detail", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
        }
        return v
    }

    override fun onStart() {
        super.onStart()
        fetchPesanData()
    }

    private fun fetchPesanData() {
        val adapter = b.lvKritikSaran.adapter as PesanAdapter
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pesanData = ArrayList<Pesan>()
                for (childSnapshot in dataSnapshot.children) {
                    val pesan = childSnapshot.getValue(Pesan::class.java)
                    if (pesan != null) {
                        pesanData.add(pesan)
                    }
                }
                adapter.clear()
                adapter.addAll(pesanData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// ========================================================
data class Pesan(
    val id: String,
    val nama: String,
    val bidang: String,
    val pesan: String,
    val waktu_dikirim: String,
    var status: String,
    var balasan: String,
    var waktu_dibalas: String
) {
    constructor() : this("", "", "", "","", "", "", "")
}

// ========================================================
class PesanAdapter(context: Context, data: List<Pesan>) :
    ArrayAdapter<Pesan>(context, R.layout.item_kritiksaran, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_kritiksaran, parent, false)

        val pesan = getItem(position)
        val idTV: TextView = itemView.findViewById(R.id.tvIdPesan)
        val namaTV: TextView = itemView.findViewById(R.id.tvNamaPesan)
        val pesanTV: TextView = itemView.findViewById(R.id.tvPesan)
        val bidangTV: TextView = itemView.findViewById(R.id.tvBidangPesan)
        val tanggalTV: TextView = itemView.findViewById(R.id.tvTanggalPesan)
        val btnStatus: Button = itemView.findViewById(R.id.btnStatus)

        if (pesan != null) {
            idTV.text = "ID: ${pesan.id}"
            namaTV.text = "Nama : ${pesan.nama}"
            pesanTV.text = "Pesan : ${pesan.pesan}"
            bidangTV.text = "Bidang : ${pesan.bidang}"
            tanggalTV.text = "Tanggal dikirim: ${pesan.waktu_dikirim}"
            btnStatus.text = "${pesan.status}"
            val status = pesan.status
            if (status == "Belum dicek") {
                btnStatus.setBackgroundColor(Color.RED)
                btnStatus.setTextColor(Color.WHITE)
            } else if (status == "Sudah dibaca") {
                btnStatus.setBackgroundColor(Color.YELLOW)
                btnStatus.setTextColor(Color.BLACK)
            } else if (status == "Sudah dibalas") {
                btnStatus.setBackgroundColor(Color.GREEN)
                btnStatus.setTextColor(Color.BLACK)
            }
        }
        return itemView
    }
}