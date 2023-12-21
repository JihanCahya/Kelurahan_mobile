package com.polinema.uas.sipkburengan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.bumptech.glide.Glide
import com.polinema.uas.sipkburengan.databinding.ActivityInformasiAdminBinding

class InformasiAdminActivity : Fragment() {

    lateinit var thisParent: DashboardAdminActivity
    private lateinit var b : ActivityInformasiAdminBinding
    lateinit var v: View
    private lateinit var db: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as DashboardAdminActivity
        b = ActivityInformasiAdminBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("informasi")
        val adapter = InformasiAdapter(requireContext(), ArrayList())

        b.lvInformasi.adapter = adapter

        b.lvInformasi.setOnItemClickListener { parent, view, position, id ->
            val selectedInformasi = adapter.getItem(position)
            if (selectedInformasi != null) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Detail Informasi")
                builder.setMessage("ID : ${selectedInformasi.id}\nJudul : ${selectedInformasi.judul}\nJenis : ${selectedInformasi.jenis}\nDeskripsi : ${selectedInformasi.deskripsi}\nTanggal : ${selectedInformasi.tanggal}")
                builder.setPositiveButton("Hapus") { dialog, _ ->
                    val informasiRef = db.child(selectedInformasi.id)
                    informasiRef.removeValue()
                    fetchInformasiData()
                    Toast.makeText(requireContext(), "Data Berhasil Dihapus", Toast.LENGTH_LONG)
                        .show()
                    dialog.dismiss()
                }
                builder.setNegativeButton("Edit") { dialog, _ ->
                    val intent = Intent(requireContext(), EditInformasiActivity::class.java)
                    intent.putExtra("ID_INFORMASI", selectedInformasi.id)
                    startActivity(intent)
                }
                builder.show()
            }
        }

        b.btnInsertInformasi.setOnClickListener {
            val intent = Intent(requireContext(), TambahInformasiActivity::class.java)
            startActivity(intent)
        }
        return v
    }

    override fun onStart() {
        super.onStart()
        fetchInformasiData()
    }

    private fun fetchInformasiData() {
        val adapter = b.lvInformasi.adapter as InformasiAdapter
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val informasiData = ArrayList<Informasi>()
                for (childSnapshot in dataSnapshot.children) {
                    val informasi = childSnapshot.getValue(Informasi::class.java)
                    if (informasi != null) {
                        informasiData.add(informasi)
                    }
                }
                adapter.clear()
                adapter.addAll(informasiData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// =======================================================
data class Informasi(
    val id: String,
    val judul: String,
    val jenis: String,
    val tanggal: String,
    val deskripsi: String,
    var imageUrl: String
) {
    constructor() : this("", "", "", "","", "")
}

// ========================================================
class InformasiAdapter(context: Context, data: List<Informasi>) :
    ArrayAdapter<Informasi>(context, R.layout.item_informasi, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_informasi, parent, false)

        val informasi = getItem(position)
        val idTV: TextView = itemView.findViewById(R.id.tvIdInformasi)
        val judulTV: TextView = itemView.findViewById(R.id.tvJudulInformasi)
        val jenisTV: TextView = itemView.findViewById(R.id.tvJenisInformasi)
        val tanggalTV: TextView = itemView.findViewById(R.id.tvTanggalInformasi)
        val imageView: ImageView = itemView.findViewById(R.id.imvInformasi) // ImageView untuk menampilkan gambar

        if (informasi != null) {
            idTV.text = "ID: ${informasi.id}"
            judulTV.text = "Judul: ${informasi.judul}"
            jenisTV.text = "Jenis: ${informasi.jenis}"
            tanggalTV.text = "Tanggal:${informasi.tanggal}"
        }

        // Menggunakan Glide untuk mengunduh dan menampilkan gambar dari URL
        if (informasi != null && informasi.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(informasi.imageUrl)
                .into(imageView)
        } else {
            // Atur gambar default jika URL gambar tidak ada atau kosong
            imageView.setImageResource(R.drawable.ic_launcher_background) // Ganti dengan gambar default yang sesuai
        }

        return itemView
    }
}