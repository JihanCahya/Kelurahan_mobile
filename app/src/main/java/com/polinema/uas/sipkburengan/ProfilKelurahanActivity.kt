package com.polinema.uas.sipkburengan

import android.content.Context
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
import com.polinema.uas.sipkburengan.databinding.ActivityProfilKelurahanBinding

class ProfilKelurahanActivity : Fragment() {

    private lateinit var b: ActivityProfilKelurahanBinding
    lateinit var thisParent: MainActivity
    lateinit var v: View
    lateinit var db : DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        b = ActivityProfilKelurahanBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("pegawai")
        val adapter = strukturAdapter(requireContext(), ArrayList())

        b.lvPegawaiKelurahan.adapter = adapter

        return v
    }

    override fun onStart() {
        super.onStart()
        fetchStrukturData()
    }

    private fun fetchStrukturData() {
        val adapter = b.lvPegawaiKelurahan.adapter as strukturAdapter
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pegawaiData = ArrayList<Pegawai>()
                for (childSnapshot in dataSnapshot.children) {
                    val pegawai = childSnapshot.getValue(Pegawai::class.java)
                    if (pegawai != null) {
                        pegawaiData.add(pegawai)
                    }
                }
                adapter.clear()
                adapter.addAll(pegawaiData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// ========================================================
class strukturAdapter(context: Context, data: List<Pegawai>) :
    ArrayAdapter<Pegawai>(context, R.layout.item_pegawai, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_pegawai, parent, false)

        val pegawai = getItem(position)
        val idTV: TextView = itemView.findViewById(R.id.tvIdPegawai)
        val namaTV: TextView = itemView.findViewById(R.id.tvNamaPegawai)
        val nipTV: TextView = itemView.findViewById(R.id.tvNipPegawai)
        val jabatanTV: TextView = itemView.findViewById(R.id.tvJabatanPegawai)
        val alamatTV: TextView = itemView.findViewById(R.id.tvAlamatPegawai)
        val imageView: ImageView = itemView.findViewById(R.id.imvPegawai)

        if (pegawai != null) {
            idTV.text = "ID : ${pegawai.id}"
            namaTV.text = "Nama : ${pegawai.nama}"
            nipTV.text = "NIP : ${pegawai.nip}"
            jabatanTV.text = "Jabatan: ${pegawai.jabatan}"
            alamatTV.text = "Alamat: ${pegawai.alamat}"
        }

        if (pegawai != null && pegawai.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(pegawai.imageUrl)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background)
        }

        return itemView
    }
}