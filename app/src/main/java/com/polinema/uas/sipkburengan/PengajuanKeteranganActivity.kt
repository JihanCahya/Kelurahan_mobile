package com.polinema.uas.sipkburengan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.uas.sipkburengan.databinding.ActivityPengajuanKeteranganBinding
import java.text.SimpleDateFormat
import java.util.Date

class PengajuanKeteranganActivity : AppCompatActivity() {
    private lateinit var b: ActivityPengajuanKeteranganBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUriPengantarRT: Uri? = null
    private var imageUriKTP: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private val PICK_IMAGE_REQUEST_KTP = 2

    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private fun getUserName(uid: String, onComplete: (String) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("Data_user")
        usersRef.child(uid).child("nama").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userName = dataSnapshot.getValue(String::class.java) ?: ""
                onComplete(userName)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPengajuanKeteranganBinding.inflate(layoutInflater)
        setContentView(b.root)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.keterangan_options,  // R.array.keterangan_options adalah array resource yang perlu Anda tambahkan ke file strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            b.spKet.adapter = adapter
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Pengajuan")
        storageReference = FirebaseStorage.getInstance().reference

        val btnSimpan = b.UpKeterangan

        val btnPilihGambarRT = b.UpPKeterangan
        btnPilihGambarRT.setOnClickListener {
            launchImagePicker(PICK_IMAGE_REQUEST)
        }

        val btnPilihGambarKTP = b.upKtpKeterangan
        btnPilihGambarKTP.setOnClickListener {
            launchImagePicker(PICK_IMAGE_REQUEST_KTP)
        }

        btnSimpan.setOnClickListener {
            uploadData()
        }

        b.backKeterangan.setOnClickListener {
            finish()
        }
    }

    private fun launchImagePicker(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    private fun uploadData() {
        if (imageUriPengantarRT != null && imageUriKTP != null) {
            val idPengajuan = databaseReference.push().key
            val currentDate = SimpleDateFormat("dd/MM/yyyy").format(Date())

            getUserName(uid ?: "") { userName ->
                uploadImage(idPengajuan!!, "pengantar_rt", imageUriPengantarRT!!, currentDate, userName)
                uploadImage(idPengajuan, "ktp", imageUriKTP!!, currentDate, userName)

                val pengajuan = Pengajuan(
                    idPengajuan,
                    uid ?: "",
                    userName,
                    currentDate,
                    "Belum dicek",
                    "Surat Keterangan",
                    b.spKet.selectedItem.toString(),
                    "-",
                    imageUriPengantarRT.toString(),
                    imageUriKTP.toString(),
                    "-",
                    "-",
                    "-"
                )

                databaseReference.child(idPengajuan).setValue(pengajuan)
                showSuccessDialog("Data Pengajuan berhasil diunggah!", idPengajuan)
            }
        } else {
            showErrorDialog("Pilih gambar Pengantar RT, KTP terlebih dahulu")
        }
    }

    private fun uploadImage(
        idPengajuan: String,
        imageType: String,
        imageUri: Uri,
        currentDate: String,
        userName: String
    ) {
        val timestamp = System.currentTimeMillis()
        val fileName = "$idPengajuan-$imageType-$timestamp.jpg"

        val imageRef = storageReference.child("images/pengajuan_keterangan/$fileName")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                updateDatabase(idPengajuan, imageType, imageUrl, currentDate, userName)
            }
        }.addOnFailureListener { exception ->
            showErrorDialog("Terjadi kesalahan saat mengunggah gambar: ${exception.localizedMessage}")
        }
    }

    private fun updateDatabase(
        idPengajuan: String,
        imageType: String,
        imageUrl: String,
        currentDate: String,
        userName: String
    ) {
        val databaseRef = databaseReference.child(idPengajuan)

        when (imageType) {
            "pengantar_rt" -> databaseRef.child("imageUrlPengantarRT").setValue(imageUrl)
            "ktp" -> databaseRef.child("imageUrlKTP").setValue(imageUrl)
            // Add more cases for other image types if needed
        }
    }


    data class Pengajuan(
        val id: String = "",
        val id_pengaju: String = "",
        val nama_pengaju: String = "",
        val tanggalPengajuan: String = "",
        val status: String = "",
        val surat: String = "",
        val jenisSurat: String = "",
        val keterangan: String = "",
        val imageUrlPengantarRT: String = "",
        val imageUrlKTP: String = "",
        val imageUrlKK: String = "",
        val imageUrlAkta: String = "",
        val tanggalSelesai: String = ""
    )

    private fun showSuccessDialog(message: String, idPengajuan: String) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle("BERHASIL")
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                showToast("Data Pengajuan berhasil diunggah!")
                val mainIntent = Intent(this@PengajuanKeteranganActivity, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle("ERROR")
            setMessage(message)
            setPositiveButton("Ok") { _, _ -> }
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    imageUriPengantarRT = data.data
                    b.ImvPKeterangan.setImageURI(imageUriPengantarRT)
                }
                PICK_IMAGE_REQUEST_KTP -> {
                    imageUriKTP = data.data
                    b.ImvKtpKeterangan.setImageURI(imageUriKTP)
                }
            }
        }
    }
}
