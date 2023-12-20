package com.polinema.uas.sipkburengan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.polinema.uas.sipkburengan.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityRegisterBinding
    private lateinit var db : DatabaseReference
    var user = User()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun onClick(v: View?) {

    }
}

class User {
    var nama: String? = null
    var email: String? = null
    var nik: String? = null
    var alamat: String? = null
    var no: String? = null
    var password: String? = null
}