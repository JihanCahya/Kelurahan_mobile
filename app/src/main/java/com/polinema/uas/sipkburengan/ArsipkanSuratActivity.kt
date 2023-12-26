package com.polinema.uas.sipkburengan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ArsipkanSuratActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arsipkan_surat)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnUnggahArsip -> {

            }
            R.id.btnSimpanArsip -> {

            }
            R.id.btnKembaliArsip -> {

            }
        }
    }
}