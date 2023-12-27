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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polinema.uas.sipkburengan.databinding.ActivityInformasiPenggunaBinding

class InformasiPenggunaActivity : Fragment() {

    private lateinit var b: ActivityInformasiPenggunaBinding
    lateinit var thisParent: MainActivity
    lateinit var v: View
    private lateinit var db: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        b = ActivityInformasiPenggunaBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("informasi")
        val adapter = BeritaAdapter(requireContext(), ArrayList())

        b.lvBerita.adapter = adapter
        b.lvBerita.setOnItemClickListener { parent, view, position, id ->
            val selectedInformasi = adapter.getItem(position)
            if (selectedInformasi != null){
                val intent = Intent(requireContext(), DetailInformasiActivity::class.java)
                intent.putExtra("ID_BERITA", selectedInformasi.id)
                startActivity(intent)
            }
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        fetchBeritaData()
    }

    private fun fetchBeritaData() {
        val adapter = b.lvBerita.adapter as BeritaAdapter
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val beritaData = ArrayList<Berita>()
                for (childSnapshot in dataSnapshot.children) {
                    val berita = childSnapshot.getValue(Berita::class.java)
                    if (berita != null && berita.jenis == "Berita kelurahan") {
                        beritaData.add(berita)
                    }
                }
                adapter.clear()
                adapter.addAll(beritaData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// =======================================================
data class Berita(
    val id: String,
    val judul: String,
    val jenis: String,
    val deskripsi: String,
    val tanggal: String,
    var imageUrl: String
) {
    constructor() : this( "","", "", "","", "")
}

// ========================================================
class BeritaAdapter(context: Context, data: List<Berita>) :
    ArrayAdapter<Berita>(context, R.layout.item_informasi_pengguna, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_informasi_pengguna, parent, false)

        val berita = getItem(position)
        val namaTV: TextView = itemView.findViewById(R.id.tvJudulBerita)
        val tanggalTV: TextView = itemView.findViewById(R.id.tvTanggalBerita)
        val imageView: ImageView = itemView.findViewById(R.id.imageInformasi)

        if (berita != null) {
            namaTV.text = "Judul Berita: ${berita.judul}"
            tanggalTV.text = "Tanggal : ${berita.tanggal}"
        }

        if (berita != null && berita.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(berita.imageUrl)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background)
        }

        return itemView
    }
}