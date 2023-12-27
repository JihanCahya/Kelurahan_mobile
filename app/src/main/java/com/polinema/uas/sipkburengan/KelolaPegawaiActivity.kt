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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.polinema.uas.sipkburengan.databinding.ActivityKelolaPegawaiBinding

class KelolaPegawaiActivity : Fragment() {
    lateinit var thisParent: DashboardAdminActivity
    private lateinit var b : ActivityKelolaPegawaiBinding
    lateinit var v: View
    private lateinit var db: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as DashboardAdminActivity
        b = ActivityKelolaPegawaiBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("pegawai")
        val adapter = PegawaiAdapter(requireContext(), ArrayList())

        b.lvPegawai.adapter = adapter

        b.lvPegawai.setOnItemClickListener { parent, view, position, id ->
            val selectedPegawai = adapter.getItem(position)
            if (selectedPegawai != null) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Detail Pegawai")
                builder.setMessage("Nama : ${selectedPegawai.nama}\nNIP : ${selectedPegawai.nip}\nJabatan : ${selectedPegawai.jabatan}\nAlamat : ${selectedPegawai.alamat}")
                builder.setPositiveButton("Hapus") { dialog, _ ->
                    val imageUrl = selectedPegawai.imageUrl
                    if (imageUrl != null && imageUrl.isNotEmpty()){
                        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                        storageRef.delete().addOnSuccessListener {
                            val pegawaiRef = db.child(selectedPegawai.id)
                            pegawaiRef.removeValue()
                            fetchPegawaiData()
                            Toast.makeText(requireContext(), "Data Berhasil Dihapus", Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Gagal menghapus foto dari storage", Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                        }
                    } else {
                        val pegawaiRef = db.child(selectedPegawai.id)
                        pegawaiRef.removeValue()
                        fetchPegawaiData()
                        Toast.makeText(requireContext(), "Data Berhasil Dihapus", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                }
                builder.setNegativeButton("Edit") { dialog, _ ->
                    val intent = Intent(requireContext(), EditPegawaiActivity::class.java)
                    intent.putExtra("ID_PEGAWAI", selectedPegawai.id)
                    startActivity(intent)
                }
                builder.show()
            }
        }
        b.btnTambahPegawai.setOnClickListener {
            val intent = Intent(requireContext(), TambahPegawaiActivity::class.java)
            startActivity(intent)
        }
        return v
    }

    override fun onStart() {
        super.onStart()
        fetchPegawaiData()
    }

    private fun fetchPegawaiData() {
        val adapter = b.lvPegawai.adapter as PegawaiAdapter
        db.addValueEventListener(object : ValueEventListener {
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

// =======================================================
data class Pegawai(
    val id: String,
    val nama: String,
    val nip: String,
    val jabatan: String,
    val alamat: String,
    var imageUrl: String
) {
    constructor() : this("", "", "", "","", "")
}

// ========================================================
class PegawaiAdapter(context: Context, data: List<Pegawai>) :
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