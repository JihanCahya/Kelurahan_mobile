package com.polinema.uas.sipkburengan

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polinema.uas.sipkburengan.databinding.ActivityHomeAdminBinding

class HomeAdminActivity : Fragment() {

    private lateinit var b : ActivityHomeAdminBinding
    lateinit var db : DatabaseReference
    private lateinit var db_user: DatabaseReference
    lateinit var thisParent: DashboardAdminActivity
    lateinit var v: View
    lateinit var dialog : AlertDialog.Builder
    lateinit var totalCount : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as DashboardAdminActivity
        b = ActivityHomeAdminBinding.inflate(layoutInflater)
        v = b.root

        dialog = AlertDialog.Builder(thisParent)
        db = FirebaseDatabase.getInstance().getReference("Pengajuan")

        db_user = FirebaseDatabase.getInstance().getReference("Data_user")
        val adapter = DataUserAdapter(thisParent, ArrayList())

        b.lvUserSIPK.adapter = adapter

        b.lvUserSIPK.setOnItemClickListener { parent, view, position, id ->
            val selectedUser = adapter.getItem(position)
            if (selectedUser != null) {
                val builder = AlertDialog.Builder(thisParent)
                builder.setTitle("Detail User")
                builder.setMessage("Nama : ${selectedUser.nama}\nNIK : ${selectedUser.nik}\nAlamat : ${selectedUser.alamat}\nAkses : ${selectedUser.akses}\nEmail : ${selectedUser.email}\nNo HP : ${selectedUser.no}")
                builder.setNegativeButton("Lihat foto") { dialog, _ ->
                    val foto = selectedUser.image
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(foto))
                    startActivity(intent)
                }
                builder.show()
            }
        }

        totalCount = "tes"
        get_count("Belum dicek")
        get_count("Belum terpenuhi")
        get_count("Terpenuhi")
        get_count("Sudah diambil")

        return v
    }

    override fun onStart() {
        super.onStart()
        fetchUserData()
    }

    private fun get_count(status:String) {
        db.orderByChild("status").equalTo(status)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    totalCount = dataSnapshot.childrenCount.toString()
                    if(status == "Belum dicek"){
                        b.edJumlahSuratBC.setText(totalCount)
                    } else if (status == "Belum terpenuhi"){
                        b.edJumlahSuratTTV.setText(totalCount)
                    } else if (status == "Terpenuhi"){
                        b.edSuratTV.setText(totalCount)
                    } else if (status == "Sudah diambil"){
                        b.edSuratSD.setText(totalCount)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    private fun fetchUserData() {
        val adapter = b.lvUserSIPK.adapter as DataUserAdapter
        db_user.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = ArrayList<DataUser>()
                for (childSnapshot in dataSnapshot.children) {
                    val DataUser = childSnapshot.getValue(DataUser::class.java)
                    if (DataUser != null) {
                        userData.add(DataUser)
                    }
                }
                adapter.clear()
                adapter.addAll(userData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(thisParent, "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

data class DataUser(
    val id: String,
    val nama: String,
    val nik: String,
    val email: String,
    val no: String,
    var alamat: String,
    var akses: String,
    var password: String,
    var image: String
) {
    constructor() : this("", "", "", "","", "", "", "", "")
}

// ========================================================
class DataUserAdapter(context: Context, data: List<DataUser>) :
    ArrayAdapter<DataUser>(context, R.layout.item_user, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)

        val dataUser = getItem(position)
        val idTV: TextView = itemView.findViewById(R.id.tvIdDataUser)
        val namaTV: TextView = itemView.findViewById(R.id.tvNamaDataUser)
        val nikTV: TextView = itemView.findViewById(R.id.tvNikDataUser)
        val alamatTV: TextView = itemView.findViewById(R.id.tvAlamatDataUser)
        val aksesTV: TextView = itemView.findViewById(R.id.tvAksesDataUser)
        val imageView: ImageView = itemView.findViewById(R.id.imvDataUser)

        if (dataUser != null) {
            idTV.text = "ID : ${dataUser.id}"
            namaTV.text = "Nama : ${dataUser.nama}"
            nikTV.text = "NIK : ${dataUser.nik}"
            alamatTV.text = "Alamat : ${dataUser.alamat}"
            aksesTV.text = "Akses : ${dataUser.akses}"
        }

        if (dataUser != null && dataUser.image.isNotEmpty()) {
            Glide.with(context)
                .load(dataUser.image)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background)
        }

        return itemView
    }
}