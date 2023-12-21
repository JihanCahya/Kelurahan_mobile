package com.polinema.uas.sipkburengan

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    lateinit var dialog : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dialog = AlertDialog.Builder(this)
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

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

}