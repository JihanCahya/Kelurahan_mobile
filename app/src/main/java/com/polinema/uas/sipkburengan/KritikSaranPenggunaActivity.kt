package com.polinema.uas.sipkburengan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polinema.uas.sipkburengan.databinding.ActivityKritikSaranPenggunaBinding
import java.util.Calendar

class KritikSaranPenggunaActivity : Fragment() {

    private lateinit var b: ActivityKritikSaranPenggunaBinding
    lateinit var thisParent: MainActivity
    lateinit var v: View
    private lateinit var db: DatabaseReference
    private lateinit var db_user: DatabaseReference
    private lateinit var userId: String
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val date = "$day-$month-$year"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        b = ActivityKritikSaranPenggunaBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("kritik_saran")
        db_user = FirebaseDatabase.getInstance().getReference("Data_user")

        val adapter = KritikAdapter(requireContext(), ArrayList())

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("UID", "").toString()

        b.lvKritik.adapter = adapter

        b.btnKirimKritik.setOnClickListener {
            if (isFormValid()){
                val nama = b.edNamaKritik.text.toString()
                val bidang = b.edBidangKritik.text.toString()
                val pesan = b.edPesanKritik.text.toString()

                val newRef = db.push()
                val uniqueKey = newRef.key

                val kritik = Kritik(uniqueKey.toString(), nama, bidang, pesan, "Belum dicek", date, "-","-")
                val kritikRef = db.child(uniqueKey.toString())
                kritikRef.setValue(kritik).addOnSuccessListener {
                    kosongkanData()
                    fetchKritikData()
                    Toast.makeText(requireContext(), "Data kritik saran berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Form jabatan harus diisi!!!", Toast.LENGTH_SHORT).show()
            }
        }

        loadDataUser(userId)

        return v
    }

    override fun onStart() {
        super.onStart()
        fetchKritikData()
    }

    private fun kosongkanData(){
        b.edNamaKritik.setText("")
        b.edBidangKritik.setText("")
        b.edPesanKritik.setText("")
    }

    private fun isFormValid(): Boolean {
        return (
                !b.edNamaKritik.text.toString().isEmpty()&&
                !b.edBidangKritik.text.toString().isEmpty()&&
                !b.edPesanKritik.text.toString().isEmpty()
                )
    }

    private fun loadDataUser(userId : String) {
        db_user.child(userId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val Kritik = dataSnapshot.getValue(Kritik::class.java)
                Kritik?.let {
                    with(b) {
                        edNamaKritik.setText(it.nama)
                    }
                }
            }
        }
    }

    private fun fetchKritikData() {
        val adapter = b.lvKritik.adapter as KritikAdapter
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val kritikData = ArrayList<Kritik>()
                for (childSnapshot in dataSnapshot.children) {
                    val kritik = childSnapshot.getValue(Kritik::class.java)
                    if (kritik != null) {
                        kritikData.add(kritik)
                    }
                }
                adapter.clear()
                adapter.addAll(kritikData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

//=========================================================================
data class Kritik(
    val id: String,
    val nama: String,
    val bidang: String,
    val pesan: String,
    val status: String,
    val waktu_dikirim: String,
    val balasan: String,
    var waktu_dibalas: String
) {
    constructor() : this("", "", "", "", "", "","","")
}

//=========================================================================
class KritikAdapter(context: Context, data: List<Kritik>) :
    ArrayAdapter<Kritik>(context, R.layout.item_kritik_masyarakat, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_kritik_masyarakat, parent, false)

        val kritik = getItem(position)
        val namaTV: TextView = itemView.findViewById(R.id.tvNamaKritik)
        val bidangTV: TextView = itemView.findViewById(R.id.tvBidangKritik)
        val kritikTV: TextView = itemView.findViewById(R.id.tvPesanKritik)
        val statusTV: TextView = itemView.findViewById(R.id.tvStatusKritik)

        if (kritik != null) {
            namaTV.text = "Nama : ${kritik.nama}"
            bidangTV.text = "Bidang : ${kritik.bidang}"
            kritikTV.text = "Pesan : ${kritik.pesan}"
            val statusKritik = kritik.status
            if(statusKritik == "Sudah dibalas"){
                statusTV.text = "Balasan : ${kritik.balasan}"
            } else {
                statusTV.text = "Status : ${kritik.status}"
            }
        }

        return itemView
    }
}