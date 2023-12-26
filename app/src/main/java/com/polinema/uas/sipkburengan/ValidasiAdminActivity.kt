package com.polinema.uas.sipkburengan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.polinema.uas.sipkburengan.databinding.ActivityValidasiAdminBinding

class ValidasiAdminActivity : Fragment() {
    lateinit var thisParent: DashboardAdminActivity
    private lateinit var b : ActivityValidasiAdminBinding
    lateinit var v: View
    private lateinit var db: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as DashboardAdminActivity
        b = ActivityValidasiAdminBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("pengajuan")
//        val adapter = ValidasiAdapter(requireContext(), ArrayList())
//
//        b.lvValidasi.adapter = adapter
//        b.lvValidasi.setOnItemClickListener { parent, view, position, id ->
//            val selectedSurat = adapter.getItem(position)
//            if (selectedSurat != null){
//                val builder = AlertDialog.Builder(requireContext())
//                builder.setTitle("Detail Surat")
//                builder.setMessage("Nama : ${selectedSurat.id_pengaju}\nSurat : ${selectedSurat.surat}\nJenis Surat : ${selectedSurat.jenisSurat}\nTanggal Pengajuan : ${selectedSurat.tanggalPengajuan}\nStatus : ${selectedSurat.status}")
//                val status = selectedSurat.status
//                if (status == "Belum dicek"){
//
//                } else if (status == "Belum terpenuhi"){
//
//                } else if (status == "Sudah diperbarui"){
//
//                } else if (status == "Terpenuhi"){
//
//                } else if (status == "Sudah diambil"){
//
//                }
//            }
//        }

        return v
    }
}