package com.polinema.uas.sipkburengan

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationBarView
import com.polinema.uas.sipkburengan.databinding.ActivityDashboardAdminBinding

class DashboardAdminActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var b : ActivityDashboardAdminBinding
    lateinit var fragInformasi : InformasiAdminActivity
    lateinit var fragStruktur : StrukturAdminActivity
    lateinit var fragLayanan : LayananAdminActivity
    lateinit var ft : FragmentTransaction
    lateinit var dialog : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(b.root)

        dialog = AlertDialog.Builder(this)

        b.bottomNavigation.setOnItemSelectedListener(this)
        fragInformasi = InformasiAdminActivity()
        fragStruktur = StrukturAdminActivity()
        fragLayanan = LayananAdminActivity()
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
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, fragStruktur).commit()
                b.frameLayout.setBackgroundColor(
                    Color.argb(245,225,255,255)
                )
                b.frameLayout.visibility = View.VISIBLE
            }
            R.id.menuLayanan -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, fragLayanan).commit()
                b.frameLayout.setBackgroundColor(
                    Color.argb(245,225,255,255)
                )
                b.frameLayout.visibility = View.VISIBLE
            }
            R.id.menuHome -> b.frameLayout.visibility = View.GONE
        }
        return true
    }
}