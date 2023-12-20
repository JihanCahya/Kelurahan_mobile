package com.polinema.uas.sipkburengan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.polinema.uas.sipkburengan.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityRegisterBinding
    private lateinit var db : DatabaseReference
    private lateinit var auth : FirebaseAuth
    var currentUser : FirebaseUser? = null
    private var user = User()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        auth = Firebase.auth
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        b.btnRegister.setOnClickListener(this)
        b.tvLogin.setOnClickListener(this)
    }
    override fun onStart() {
        super.onStart()
        db = FirebaseDatabase.getInstance().getReference("Data_user")
    }

    fun kosongkanData(){
        b.edNik.setText("")
        b.edNamaLengkap.setText("")
        b.edEmail.setText("")
        b.edAlamat.setText("")
        b.edTelepon.setText("")
        b.edPasswordR.setText("")
    }

    private fun isFormValid(): Boolean {
        return (
                !b.edNik.text.toString().isEmpty() &&
                !b.edNamaLengkap.text.toString().isEmpty() &&
                !b.edEmail.text.toString().isEmpty() &&
                !b.edAlamat.text.toString().isEmpty() &&
                !b.edTelepon.text.toString().isEmpty() &&
                !b.edPasswordR.text.toString().isEmpty()
                )
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btnRegister -> {
                if (isFormValid()) {
                    auth.createUserWithEmailAndPassword(
                        b.edEmail.text.toString(),
                        b.edPasswordR.text.toString()
                    ).addOnCompleteListener {
                        if(it.isSuccessful){
                            currentUser = auth.currentUser
                            if(currentUser != null){
                                currentUser!!.updateProfile(
                                    userProfileChangeRequest {
                                        displayName = b.edNamaLengkap.text.toString()
                                    }
                                )
                                currentUser!!.sendEmailVerification()
                            }
                            val newRef = db.push()
                            val uniqueKey = newRef.key

                            user.nik = b.edNik.text.toString()
                            user.nama = b.edNamaLengkap.text.toString()
                            user.email = b.edEmail.text.toString()
                            user.alamat = b.edAlamat.text.toString()
                            user.no = b.edTelepon.text.toString()
                            user.akses = "Masyarakat"
                            user.password = b.edPasswordR.text.toString()
                            user.id = uniqueKey
                            newRef.setValue(user).addOnSuccessListener {
                                Toast.makeText(this, "Berhasil mendaftarkan akun, silahkan cek email anda untuk verifikasi", Toast.LENGTH_LONG).show()
                                kosongkanData()
                            }.addOnFailureListener {
                                Toast.makeText(this, "Gagal menyimpan data ke database", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this, "Tidak dapat mendaftar, periksa data", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Snackbar.make(b.root, "Semua form harus diisi !!!", Snackbar.LENGTH_SHORT).show()
                }
            }
            R.id.tvLogin -> {
                Toast.makeText(this,"LOGIN",Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class User {
    var id: String? = null
    var nama: String? = null
    var email: String? = null
    var nik: String? = null
    var alamat: String? = null
    var no: String? = null
    var akses: String? = null
    var password: String? = null
}