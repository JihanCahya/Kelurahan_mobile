package com.polinema.uas.sipkburengan

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.polinema.uas.sipkburengan.databinding.ActivityDashboardAdminBinding

class DashboardAdminActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var b : ActivityDashboardAdminBinding
    lateinit var db : DatabaseReference
    private lateinit var db_user: DatabaseReference
    lateinit var fragInformasi : InformasiAdminActivity
    lateinit var fragJabatan : KelolaJabatanActivity
    lateinit var fragPegawai : KelolaPegawaiActivity
    lateinit var fragKritik : KritikSaranAdminActivity
    lateinit var fragValidasi : ValidasiAdminActivity
    lateinit var fragHistory : HistoryAdminActivity
    lateinit var ft : FragmentTransaction
    lateinit var dialog : AlertDialog.Builder
    lateinit var totalCount : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(b.root)

        dialog = AlertDialog.Builder(this)
        db = FirebaseDatabase.getInstance().getReference("Pengajuan")

        db_user = FirebaseDatabase.getInstance().getReference("Data_user")
        val adapter = DataUserAdapter(this, ArrayList())

        b.lvUserSIPK.adapter = adapter

        b.lvUserSIPK.setOnItemClickListener { parent, view, position, id ->

            val selectedUser = adapter.getItem(position)
            if (selectedUser != null) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Detail User")
                builder.setMessage("Nama : ${selectedUser.nama}\nNIK : ${selectedUser.nik}\nAlamat : ${selectedUser.alamat}\nAkses : ${selectedUser.akses}\nEmail : ${selectedUser.email}\nNo HP : ${selectedUser.no}")
                builder.setNegativeButton("Edit") { dialog, _ ->
                    Toast.makeText(this, "edit data user", Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
        }

        totalCount = "tes"
        get_count("Belum dicek")
        get_count("Tidak terpenuhi")
        get_count("Terpenuhi")
        get_count("Sudah diambil")

        b.bottomNavigation.setOnItemSelectedListener(this)
        fragInformasi = InformasiAdminActivity()
        fragJabatan = KelolaJabatanActivity()
        fragPegawai = KelolaPegawaiActivity()
        fragKritik = KritikSaranAdminActivity()
        fragValidasi = ValidasiAdminActivity()
        fragHistory = HistoryAdminActivity()
    }

    private fun get_count(status:String) {
        db.orderByChild("status").equalTo(status)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    totalCount = dataSnapshot.childrenCount.toString()
                    if(status == "Belum dicek"){
                        b.edJumlahSuratBC.setText(totalCount)
                    } else if (status == "Tidak terpenuhi"){
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

    override fun onStart() {
        super.onStart()
        fetchUserData()
    }

    private fun fetchUserData() {
        val adapter = b.lvUserSIPK.adapter as DataUserAdapter
        db_user.addListenerForSingleValueEvent(object : ValueEventListener {
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
                Toast.makeText(this@DashboardAdminActivity, "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_option,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.itemLogout ->{
                dialog.setTitle("Konfirmasi").setMessage(
                    "Apakah anda yakin ingin keluar?"
                )
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Ya", btnLogout)
                    .setNegativeButton("Tidak", null)
                dialog.show()
            }
            R.id.itemEditProfil -> {
                val intent = Intent(this, EditProfilPenggunaActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    val btnLogout = DialogInterface.OnClickListener { dialog, which ->
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("UID")
        editor.apply()

        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuInformasi -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, fragInformasi).commit()
                b.frameLayout.setBackgroundColor(
                    Color.argb(255,255,255,255)
                )
                b.frameLayout.visibility = View.VISIBLE
            }
            R.id.menuStruktur -> {
                val popupMenu = PopupMenu(this, findViewById(R.id.menuStruktur))
                popupMenu.menuInflater.inflate(R.menu.submenu_struktur, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { subItem ->
                    when (subItem.itemId) {
                        R.id.submenu1 -> {
                            ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.frameLayout, fragJabatan).commit()
                            b.frameLayout.setBackgroundColor(
                                Color.argb(255,255,255,255)
                            )
                            b.frameLayout.visibility = View.VISIBLE
                            true
                        }
                        R.id.submenu2 -> {
                            ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.frameLayout, fragPegawai).commit()
                            b.frameLayout.setBackgroundColor(
                                Color.argb(255,255,255,255)
                            )
                            b.frameLayout.visibility = View.VISIBLE
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            R.id.menuLayanan -> {
                val popupMenu = PopupMenu(this, findViewById(R.id.menuLayanan))
                popupMenu.menuInflater.inflate(R.menu.submenu_layanan, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { subItem ->
                    when (subItem.itemId) {
                        R.id.subKritik -> {
                            ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.frameLayout, fragKritik).commit()
                            b.frameLayout.setBackgroundColor(
                                Color.argb(255,255,255,255)
                            )
                            b.frameLayout.visibility = View.VISIBLE
                            true
                        }
                        R.id.subValidasi -> {
                            ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.frameLayout, fragValidasi).commit()
                            b.frameLayout.setBackgroundColor(
                                Color.argb(255,255,255,255)
                            )
                            b.frameLayout.visibility = View.VISIBLE
                            true
                        }
                        R.id.subHistory -> {
                            ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.frameLayout, fragHistory).commit()
                            b.frameLayout.setBackgroundColor(
                                Color.argb(255,255,255,255)
                            )
                            b.frameLayout.visibility = View.VISIBLE
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            R.id.menuHome -> b.frameLayout.visibility = View.GONE
        }
        return true
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