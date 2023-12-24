package com.polinema.uas.sipkburengan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.uas.sipkburengan.databinding.ActivityPengajuanKkBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PengajuanKKActivity : AppCompatActivity() {
    private lateinit var b: ActivityPengajuanKkBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUriPengantarRT: Uri? = null
    private var imageUriKTP: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPengajuanKkBinding.inflate(layoutInflater)
        setContentView(b.root)

        databaseReference = FirebaseDatabase.getInstance().getReference("Pengajuan/Pengajuan_KK")
        storageReference = FirebaseStorage.getInstance().reference

        val btnSimpan = b.UpKk

        val btnPilihGambarRT = b.UpPKk
        btnPilihGambarRT.setOnClickListener {
            launchImagePicker(PICK_IMAGE_REQUEST)
        }

        val btnPilihGambarKTP = b.UpKtpKk
        btnPilihGambarKTP.setOnClickListener {
            launchImagePicker(PICK_IMAGE_REQUEST + 1)
        }

        btnSimpan.setOnClickListener {
            if (imageUriPengantarRT != null && imageUriKTP != null) {
                val idPengajuan = databaseReference.push().key
                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                uploadImage(idPengajuan!!, "pengantar_rt", imageUriPengantarRT!!, currentDate)
                uploadImage(idPengajuan, "ktp", imageUriKTP!!, currentDate)

                val pengajuan = Pengajuan(
                    idPengajuan,
                    uid ?: "", // Menggunakan UID pengguna
                    currentDate,
                    "Belum dicek",
                    "Surat Pengajuan KK",
                    imageUriPengantarRT.toString(),
                    imageUriKTP.toString()
                )

                databaseReference.child(idPengajuan).setValue(pengajuan)
                showSuccessDialog("Data Pengajuan KK berhasil diunggah!")
            } else {
                showErrorDialog("Pilih gambar Pengantar RT dan KTP terlebih dahulu")
            }
        }
    }

    private fun uploadImage(idPengajuan: String, imageType: String, imageUri: Uri, currentDate: String) {
        val timestamp = System.currentTimeMillis()
        val fileName = "$idPengajuan-$imageType-$timestamp.jpg"

        val imageRef = storageReference.child("images/pengajuan_kk/$fileName")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
            }
        }.addOnFailureListener { exception ->
            showErrorDialog("Terjadi kesalahan saat mengunggah gambar: ${exception.localizedMessage}")
        }
    }

    data class Pengajuan(
        val id: String = "",
        val id_pengaju: String = "",
        val tanggalPengajuan: String = "",
        val status: String = "",
        val keterangan: String = "",
        val imageUrlPengantarRT: String = "",
        val imageUrlKTP: String = ""
    )

    private fun showSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle("BERHASIL")
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                showToast("Data Pengajuan KK berhasil diunggah!")
                val mainIntent = Intent(this@PengajuanKKActivity, MainActivity::class.java)
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

    private fun launchImagePicker(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/pengajuan_kk/*"
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    imageUriPengantarRT = data.data
                    b.ImvPKk.setImageURI(imageUriPengantarRT)
                }
                PICK_IMAGE_REQUEST + 1 -> {
                    imageUriKTP = data.data
                    b.ImvKtpKk.setImageURI(imageUriKTP)
                }
            }
        }
    }
}
