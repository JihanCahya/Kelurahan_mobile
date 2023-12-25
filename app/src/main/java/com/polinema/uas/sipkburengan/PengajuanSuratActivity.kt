package com.polinema.uas.sipkburengan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.polinema.uas.sipkburengan.databinding.ActivityPengajuanSuratBinding

class PengajuanSuratActivity : Fragment(), View.OnClickListener {
    private lateinit var b: ActivityPengajuanSuratBinding
    lateinit var thisParent: MainActivity
    lateinit var v: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        b = ActivityPengajuanSuratBinding.inflate(layoutInflater)
        v = b.root

        b.btnSuratKK.setOnClickListener(this)
        b.btnSuratKTP.setOnClickListener(this)
        b.btnSuratKeterangan.setOnClickListener(this)

        return v
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnSuratKK -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Surat pengajuan KK")
                builder.setMessage("Persyaratan surat :\n1. Foto copy KTP\n2. Surat pengantar RT")
                builder.setPositiveButton("Ajukan") { dialog, _ ->
                    val intent = Intent(requireContext(), PengajuanKKActivity::class.java)
                    startActivity(intent)
                }
                builder.setNegativeButton("Tutup") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            }
            R.id.btnSuratKTP -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Surat pengajuan KTP")
                builder.setMessage("Persyaratan surat :\n1. Foto copy KK\n2. Foto copy Akta Kelahiran\n3. KIA asli/KTP lama\n4. Surat pengantar RT")
                builder.setPositiveButton("Ajukan") { dialog, _ ->
                    val intent = Intent(requireContext(), PengajuanKtpActivity::class.java)
                    startActivity(intent)
                }
                builder.setNegativeButton("Tutup") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            }
            R.id.btnSuratKeterangan -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Surat keterangan")
                builder.setMessage("Persyaratan surat :\n1. Foto copy KK\n2. Foto copy Akta Kelahiran\n3. KIA asli/KTP lama\n4. Surat pengantar RT")
                builder.setPositiveButton("Ajukan") { dialog, _ ->
                    val intent = Intent(requireContext(), PengajuanKeteranganActivity::class.java)
                    startActivity(intent)
                }
                builder.setNegativeButton("Tutup") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }
}