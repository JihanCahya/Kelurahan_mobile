package com.polinema.uas.sipkburengan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.polinema.uas.sipkburengan.databinding.ActivityBantuanPenggunaBinding

class BantuanPenggunaActivity : Fragment() {

    private lateinit var b: ActivityBantuanPenggunaBinding
    lateinit var thisParent: MainActivity
    lateinit var v: View
    private lateinit var db: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        b = ActivityBantuanPenggunaBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("informasi")
        val adapter = BantuanAdapter(requireContext(), ArrayList())

        b.lvInformasi.adapter = adapter
        b.lvInformasi.setOnItemClickListener { parent, view, position, id ->
            val selectedBantuan = adapter.getItem(position)
            if (selectedBantuan != null){
                val intent = Intent(requireContext(), DetailBantuanAcitivity::class.java)
                intent.putExtra("ID_BANTUAN", selectedBantuan.id)
                startActivity(intent)
            }
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        fetchBantuanData()
    }

    private fun fetchBantuanData() {
        val adapter = b.lvInformasi.adapter as BantuanAdapter
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bantuanData = ArrayList<Bantuan>()
                for (childSnapshot in dataSnapshot.children) {
                    val bantuan = childSnapshot.getValue(Bantuan::class.java)
                    if (bantuan != null && bantuan.jenis == "Informasi bantuan") {
                        bantuanData.add(bantuan)
                    }
                }
                adapter.clear()
                adapter.addAll(bantuanData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// =======================================================
data class Bantuan(
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
class BantuanAdapter(context: Context, data: List<Bantuan>) :
    ArrayAdapter<Bantuan>(context, R.layout.item_bantuan_pengguna, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_bantuan_pengguna, parent, false)

        val bantuan = getItem(position)
        val namaTV: TextView = itemView.findViewById(R.id.tvJudulBantuan)
        val tanggalTV: TextView = itemView.findViewById(R.id.tvTanggalBantuan)
        val imageView: ImageView = itemView.findViewById(R.id.imageBantuan)

        if (bantuan != null) {
            namaTV.text = "Judul Berita: ${bantuan.judul}"
            tanggalTV.text = "Tanggal : ${bantuan.tanggal}"
        }

        if (bantuan != null && bantuan.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(bantuan.imageUrl)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background)
        }

        return itemView
    }
}