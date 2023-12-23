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
import com.polinema.uas.sipkburengan.databinding.ActivityKelolaJabatanBinding

class KelolaJabatanActivity : Fragment(), View.OnClickListener {

    lateinit var thisParent: DashboardAdminActivity
    private lateinit var b : ActivityKelolaJabatanBinding
    lateinit var v: View
    private lateinit var db: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as DashboardAdminActivity
        b = ActivityKelolaJabatanBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("jabatan")
        val adapter = JabatanAdapter(requireContext(), ArrayList())

        b.lvJabatan.adapter = adapter
        b.btnTambahJabatan.setOnClickListener(this)
        b.btnEditJabatan.setOnClickListener(this)
        b.btnHapusJabatan.setOnClickListener(this)
        b.btnKosongkanJabatan.setOnClickListener(this)

        b.lvJabatan.setOnItemClickListener { parent, view, position, id ->
            val selectedJabatan = adapter.getItem(position)
            val idJabatan = selectedJabatan?.id
            loadJabatanData(idJabatan.toString())
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        fetchInformasiData()
    }

    private fun kosongkanData(){
        b.edIdJabatan.setText("")
        b.edJabatan.setText("")
    }

    private fun isFormValid(): Boolean {
        return (
                !b.edJabatan.text.toString().isEmpty()
                )
    }


    private fun loadJabatanData(id : String) {
        db.child(id).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val jabatan = dataSnapshot.getValue(Jabatan::class.java)
                jabatan?.let {
                    with(b) {
                        edIdJabatan.setText(it.id)
                        edJabatan.setText(it.jabatan)
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnTambahJabatan -> {
                if (isFormValid()){
                    val edJabatan = b.edJabatan.text.toString()
                    val newRef = db.push()
                    val uniqueKey = newRef.key

                    val jabatan = Jabatan(uniqueKey.toString(), edJabatan)
                    val jabatanRef = db.child(uniqueKey.toString())
                    jabatanRef.setValue(jabatan).addOnSuccessListener {
                        kosongkanData()
                        fetchInformasiData()
                        Toast.makeText(requireContext(), "Data jabatan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Form jabatan harus diisi!!!", Toast.LENGTH_SHORT).show()
                }

            }
            R.id.btnEditJabatan -> {
                if (isFormValid()){
                    val idJabatanE = b.edIdJabatan.text.toString()
                    val edJabatanE = b.edJabatan.text.toString()

                    val jabatan = Jabatan(idJabatanE, edJabatanE)
                    db.child(idJabatanE).setValue(jabatan).addOnSuccessListener {
                        kosongkanData()
                        fetchInformasiData()
                        Toast.makeText(requireContext(), "Data jabatan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Pilih data yang ingin diedit", Toast.LENGTH_SHORT).show()
                }

            }
            R.id.btnHapusJabatan -> {
                if (isFormValid()){
                    val idJabatanH = b.edIdJabatan.text.toString()

                    val petugasRef = db.child(idJabatanH)
                    petugasRef.removeValue().addOnSuccessListener {
                        kosongkanData()
                        fetchInformasiData()
                        Toast.makeText(requireContext(), "Data jabatan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Pilih data yang ingin dihapus", Toast.LENGTH_SHORT).show()
                }

            }
            R.id.btnKosongkanJabatan -> {
                kosongkanData()
            }
        }
    }

    private fun fetchInformasiData() {
        val adapter = b.lvJabatan.adapter as JabatanAdapter
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val jabatanData = ArrayList<Jabatan>()
                for (childSnapshot in dataSnapshot.children) {
                    val jabatan = childSnapshot.getValue(Jabatan::class.java)
                    if (jabatan != null) {
                        jabatanData.add(jabatan)
                    }
                }
                adapter.clear()
                adapter.addAll(jabatanData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

//=========================================================================
data class Jabatan(
    val id: String,
    val jabatan: String,
) {
    constructor() : this("", "")
}

//=========================================================================
class JabatanAdapter(context: Context, data: List<Jabatan>) :
    ArrayAdapter<Jabatan>(context, R.layout.item_jabatan, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_jabatan, parent, false)

        val jabatan = getItem(position)
        val jabatanTV: TextView = itemView.findViewById(R.id.tvJabatan)

        if (jabatan != null) {
            jabatanTV.text = "Jabatan : ${jabatan.jabatan}"
        }

        return itemView
    }
}