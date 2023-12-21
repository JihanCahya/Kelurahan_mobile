package com.polinema.uas.sipkburengan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.polinema.uas.sipkburengan.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    private var currentUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        auth = Firebase.auth
        b.tvRegister.setOnClickListener(this)
        b.btnLogin.setOnClickListener(this)

    }

    fun kosongkanData(){
        b.edEmailL.setText("")
        b.edPasswordL.setText("")
    }

    private fun isFormValid(): Boolean {
        return (
                !b.edEmailL.text.toString().isEmpty() &&
                !b.edPasswordL.text.toString().isEmpty()
                )
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnLogin -> {
                if (isFormValid()){
                    auth.signInWithEmailAndPassword(
                        b.edEmailL.text.toString(),
                        b.edPasswordL.text.toString()
                    ).addOnCompleteListener {
                        if(it.isSuccessful){
                            currentUser = auth.currentUser
                            if(currentUser != null){
                                if(currentUser!!.isEmailVerified){
                                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "Email anda belum terverifikasi", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Username/password anda salah", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Snackbar.make(b.root, "Semua form harus diisi !!!", Snackbar.LENGTH_SHORT).show()
                }
            }
            R.id.tvRegister -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }
}