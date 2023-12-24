package com.polinema.uas.sipkburengan

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationBarView
import com.polinema.uas.sipkburengan.databinding.ActivityDashboardAdminBinding

class DashboardAdminActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var b : ActivityDashboardAdminBinding
    lateinit var fragInformasi : InformasiAdminActivity
    lateinit var fragJabatan : KelolaJabatanActivity
    lateinit var fragPegawai : KelolaPegawaiActivity
    lateinit var fragKritik : KritikSaranAdminActivity
    lateinit var fragValidasi : ValidasiAdminActivity
    lateinit var fragHistory : HistoryAdminActivity
    lateinit var ft : FragmentTransaction
    lateinit var dialog : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(b.root)

        dialog = AlertDialog.Builder(this)

        b.bottomNavigation.setOnItemSelectedListener(this)
        fragInformasi = InformasiAdminActivity()
        fragJabatan = KelolaJabatanActivity()
        fragPegawai = KelolaPegawaiActivity()
        fragKritik = KritikSaranAdminActivity()
        fragValidasi = ValidasiAdminActivity()
        fragHistory = HistoryAdminActivity()
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
                    Color.argb(245,225,255,255)
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
                                Color.argb(245,225,255,255)
                            )
                            b.frameLayout.visibility = View.VISIBLE
                            true
                        }
                        R.id.submenu2 -> {
                            ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.frameLayout, fragPegawai).commit()
                            b.frameLayout.setBackgroundColor(
                                Color.argb(245,225,255,255)
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
                                Color.argb(245,225,255,255)
                            )
                            b.frameLayout.visibility = View.VISIBLE
                            true
                        }
                        R.id.subValidasi -> {
                            ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.frameLayout, fragValidasi).commit()
                            b.frameLayout.setBackgroundColor(
                                Color.argb(245,225,255,255)
                            )
                            b.frameLayout.visibility = View.VISIBLE
                            true
                        }
                        R.id.subHistory -> {
                            ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.frameLayout, fragHistory).commit()
                            b.frameLayout.setBackgroundColor(
                                Color.argb(245,225,255,255)
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