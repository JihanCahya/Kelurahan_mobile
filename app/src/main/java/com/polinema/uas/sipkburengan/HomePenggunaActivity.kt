package com.polinema.uas.sipkburengan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.polinema.uas.sipkburengan.databinding.ActivityHomePenggunaBinding

class HomePenggunaActivity : Fragment(), View.OnClickListener {

    private lateinit var b : ActivityHomePenggunaBinding
    lateinit var thisParent: MainActivity
    lateinit var v: View
    private lateinit var db: DatabaseReference
    lateinit var uid : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        b = ActivityHomePenggunaBinding.inflate(layoutInflater)
        v = b.root

        db = FirebaseDatabase.getInstance().getReference("Data_user")

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        uid = sharedPreferences.getString("UID", "").toString()

        loadUser()

        b.imvBeritaHome.setOnClickListener(this)
        b.imvKritikHome.setOnClickListener(this)
        b.imvLayananHome.setOnClickListener(this)

        return v
    }

    private fun loadUser() {
        db.child(uid).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val userProfile = dataSnapshot.getValue(UserProfile::class.java)
                userProfile?.let {
                    with(b) {
                        textView5.setText(it.nama)

                        val checkImage = it.image
                        if(checkImage == "default"){
                            imageView8.setImageResource(R.drawable.ic_launcher_background)
                        } else {
                            Glide.with(this@HomePenggunaActivity)
                                .load(userProfile.image)
                                .into(imageView8)
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val bottomNavigationView: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
        when(v?.id){
            R.id.imvBeritaHome -> {
                val informasiFragment = InformasiPenggunaActivity()
                fragmentTransaction.replace(R.id.frameLayout1, informasiFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                bottomNavigationView.selectedItemId = R.id.itemInformasi
            }
            R.id.imvLayananHome -> {
                val layananFragment = PengajuanSuratActivity()
                fragmentTransaction.replace(R.id.frameLayout1, layananFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                bottomNavigationView.selectedItemId = R.id.itemAdministrasi
            }
            R.id.imvKritikHome -> {
                val kritikFragment = KritikSaranPenggunaActivity()
                fragmentTransaction.replace(R.id.frameLayout1, kritikFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                bottomNavigationView.selectedItemId = R.id.itemProfil
            }
        }
    }
}