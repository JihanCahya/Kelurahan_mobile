package com.polinema.uas.sipkburengan

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.uas.sipkburengan.databinding.ActivityEditProfilPenggunaBinding

class EditProfilPenggunaActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var b : ActivityEditProfilPenggunaBinding
    private lateinit var db: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var userId: String
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditProfilPenggunaBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = FirebaseDatabase.getInstance().getReference("Data_user")
        storage = FirebaseStorage.getInstance().reference

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("UID", "").toString()

        loadUserData()

        b.btnKembaliUser.setOnClickListener(this)
        b.btnUnggahProfilUser.setOnClickListener(this)
        b.btnSimpanUser.setOnClickListener(this)
    }

    private fun isFormValid(): Boolean {
        return (
                !b.edNamaUser.text.toString().isEmpty() &&
                !b.edNikUser.text.toString().isEmpty() &&
                !b.edEmailUser.text.toString().isEmpty() &&
                !b.edTeleponUser.text.toString().isEmpty() &&
                !b.edAlamatUser.text.toString().isEmpty()
                )
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnUnggahProfilUser -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
            R.id.btnKembaliUser -> {
                finish()
            }
            R.id.btnSimpanUser -> {
                if (isFormValid()){
                    b.progressBar8.visibility = View.VISIBLE
                    val id = b.edIdUser.text.toString()
                    val nama = b.edNamaUser.text.toString()
                    val nik = b.edNikUser.text.toString()
                    val email = b.edEmailUser.text.toString()
                    val telepon = b.edTeleponUser.text.toString()
                    val alamat = b.edAlamatUser.text.toString()
                    val akses = b.edAksesUser.text.toString()
                    val password = b.edPasswordUser.text.toString()
                    val image = b.edImageUser.text.toString()

                    val UserProfile = UserProfile(id, nama, nik, email, telepon, alamat, akses, password, image)

                    if (imageUri != null){
                        val imageRef = storage.child("user/$id.jpg")
                        val uploadTask = imageRef.putFile(imageUri!!)

                        uploadTask.addOnSuccessListener { _ ->
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                UserProfile.image = uri.toString()
                                saveUpdatedUser(UserProfile)
                            }
                        }.addOnFailureListener { exception ->
                            showErrorDialog("Terjadi kesalahan saat mengunggah gambar: ${exception.message}")
                        }
                    } else {
                        saveUpdatedUser(UserProfile)
                    }
                } else {
                    Toast.makeText(this, "Semua form harus diisi !!!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            b.tvNamaProfilUser.text = "Foto telah diunggah"
        }
    }

    private fun loadUserData() {
        db.child(userId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val userProfile = dataSnapshot.getValue(UserProfile::class.java)
                userProfile?.let {
                    with(b) {
                        edIdUser.setText(it.id)
                        edNamaUser.setText(it.nama)
                        edNikUser.setText(it.nik)
                        edEmailUser.setText(it.email)
                        edTeleponUser.setText(it.no)
                        edAlamatUser.setText(it.alamat)
                        edAksesUser.setText(it.akses)
                        edPasswordUser.setText(it.password)
                        edImageUser.setText(it.image)

                        val checkImage = it.image
                        if(checkImage == "default"){
                            imProfilUser.setImageResource(R.drawable.ic_launcher_background)
                        } else {
                            Glide.with(this@EditProfilPenggunaActivity)
                                .load(userProfile.image)
                                .into(imProfilUser)
                        }
                    }
                }
            }
        }
    }

    private fun saveUpdatedUser(user: UserProfile) {
        db.child(userId).setValue(user)
        showSuccessDialog()
    }

    private fun showSuccessDialog() {
        b.progressBar8.visibility = View.GONE
        AlertDialog.Builder(this).apply {
            setTitle("BERHASIL")
            setMessage("Data informasi berhasil diperbarui !")
            setPositiveButton("Ya") { _, _ -> }
        }.create().show()
        b.tvNamaProfilUser.setText("")
        loadUserData()
    }

    private fun showErrorDialog(errorMessage: String) {
        b.progressBar8.visibility = View.GONE
        AlertDialog.Builder(this).apply {
            setTitle("ERROR")
            setMessage(errorMessage)
            setPositiveButton("Ya") { _, _ -> }
        }.create().show()
    }
}

// =======================================================
data class UserProfile(
    val id: String,
    val nama: String,
    val nik: String,
    val email: String,
    val no: String,
    var alamat: String,
    var akses: String,
    var password: String,
    var image: String
) {
    constructor() : this("", "", "", "","", "", "", "", "")
}