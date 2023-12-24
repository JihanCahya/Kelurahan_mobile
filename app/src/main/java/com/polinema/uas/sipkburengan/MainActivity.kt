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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationBarView
import com.polinema.uas.sipkburengan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    lateinit var dialog : AlertDialog.Builder
    private lateinit var b : ActivityMainBinding
    lateinit var fragInformasi : InformasiPenggunaActivity
    lateinit var fragPengajuan : PengajuanSuratActivity
    lateinit var fragHistory : HistorySuratActivity
    lateinit var fragProfil : ProfilKelurahanActivity
    lateinit var ft : FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        dialog = AlertDialog.Builder(this)

        b.bottomNavigationView.setOnItemSelectedListener(this)
        fragInformasi = InformasiPenggunaActivity()
        fragPengajuan = PengajuanSuratActivity()
        fragHistory = HistorySuratActivity()
        fragProfil = ProfilKelurahanActivity()
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
        when(item.itemId){
            R.id.itemHome -> {
                b.frameLayout1.visibility = View.GONE
            }
            R.id.itemInformasi -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout1, fragInformasi).commit()
                b.frameLayout1.setBackgroundColor(
                    Color.argb(245,225,255,255)
                )
                b.frameLayout1.visibility = View.VISIBLE
                true
            }
            R.id.itemAdministrasi -> {
                val popupMenu = PopupMenu(this, findViewById(R.id.itemAdministrasi))
                popupMenu.menuInflater.inflate(R.menu.submenu_administrasi, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { subItem ->
                    when (subItem.itemId) {
                        R.id.subPengajuanP -> {
                            ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.frameLayout1, fragPengajuan).commit()
                            b.frameLayout1.setBackgroundColor(
                                Color.argb(245,225,255,255)
                            )
                            b.frameLayout1.visibility = View.VISIBLE
                            true
                        }
                        R.id.subHistoryP -> {
                            ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.frameLayout1, fragHistory).commit()
                            b.frameLayout1.setBackgroundColor(
                                Color.argb(245,225,255,255)
                            )
                            b.frameLayout1.visibility = View.VISIBLE
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            R.id.itemProfil -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout1, fragProfil).commit()
                b.frameLayout1.setBackgroundColor(
                    Color.argb(245,225,255,255)
                )
                b.frameLayout1.visibility = View.VISIBLE
                true
            }
        }
        return true
    }
}