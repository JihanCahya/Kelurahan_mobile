package com.polinema.uas.sipkburengan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.uas.sipkburengan.databinding.ActivityPengajuanKtpBinding
import java.text.SimpleDateFormat
import java.util.Date

class PengajuanKtpActivity : AppCompatActivity() {
    lateinit var b: ActivityPengajuanKtpBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUriPengantarRT: Uri? = null
    private var imageUriKTP: Uri? = null
    private var imageUriKK: Uri? = null
    private var imageUriAkta: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    private val uid: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPengajuanKtpBinding.inflate(layoutInflater)
        setContentView(b.root)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.ktp_options,  // R.array.keterangan_options adalah array resource yang perlu Anda tambahkan ke file strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            b.spKtp.adapter = adapter
        }

        // Inisialisasi DatabaseReference ke Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Pengajuan")

        // Inisialisasi StorageReference ke Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        val btnSimpan = b.upKtp

        // Tombol untuk memilih gambar Pengantar RT
        val btnPilihGambarRT = b.UpPKtp
        btnPilihGambarRT.setOnClickListener {
            launchImagePicker(PICK_IMAGE_REQUEST)
        }

        // Tombol untuk memilih gambar KTP
        val btnPilihGambarKTP = b.UpKtpKtp
        btnPilihGambarKTP.setOnClickListener {
            launchImagePicker(PICK_IMAGE_REQUEST + 1) // Change request code
        }

        // Tombol untuk memilih gambar KK
        val btnPilihGambarKK = b.UpKkKtp
        btnPilihGambarKK.setOnClickListener {
            launchImagePicker(PICK_IMAGE_REQUEST + 2) // Change request code
        }

        // Tombol untuk memilih gambar Akta
        val btnPilihGambarAkta = b.UpAktaKtp
        btnPilihGambarAkta.setOnClickListener {
            launchImagePicker(PICK_IMAGE_REQUEST + 3) // Change request code
        }

        btnSimpan.setOnClickListener {
            uploadData()
        }
    }

    private fun launchImagePicker(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    private fun uploadData() {
        if (imageUriPengantarRT != null && imageUriKTP != null && imageUriKK != null && imageUriAkta != null) {
            val idPengajuan = databaseReference.push().key
            val currentDate = SimpleDateFormat("dd/MM/yyyy").format(Date())

            uploadImage(idPengajuan!!, "pengantar_rt", imageUriPengantarRT!!, currentDate)
            uploadImage(idPengajuan, "ktp", imageUriKTP!!, currentDate)
            uploadImage(idPengajuan, "kk", imageUriKK!!, currentDate)
            uploadImage(idPengajuan, "akta", imageUriAkta!!, currentDate)

            val pengajuan = Pengajuan(
                idPengajuan,
                uid ?: "",
                currentDate,
                "Belum dicek",
                "Surat Pengajuan Ktp",
                b.spKtp.selectedItem.toString(),
                imageUrlPengantarRT = imageUriPengantarRT.toString(),
                imageUrlKTP = imageUriKTP.toString(),
                imageUrlKK = imageUriKK.toString(),
                imageUrlAkta = imageUriAkta.toString()
            )

            showSuccessDialog("Data Pengajuan berhasil diunggah!", idPengajuan)
        } else {
            showErrorDialog("Pilih gambar Pengantar RT, KTP, KK, dan Akta terlebih dahulu")
        }
    }

    private fun uploadImage(idPengajuan: String, imageType: String, imageUri: Uri, currentDate: String) {
        val timestamp = System.currentTimeMillis() // timestamp untuk menyertakan waktu unik

        // Format nama file dengan menambahkan timestamp dan jenis file ke dalamnya
        val fileName = "$idPengajuan-$imageType-$timestamp.jpg"

        val imageRef = storageReference.child("images/pengajuan_ktp/$fileName")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()

                // Setelah mendapatkan URL gambar yang benar, simpan data ke Firebase Database
                val pengajuan = Pengajuan(
                    idPengajuan,
                    uid ?: "",
                    currentDate,
                    "Belum dicek",
                    "Surat Pengajuan Ktp",
                    b.spKtp.selectedItem.toString(),
                    imageUrlPengantarRT = imageUriPengantarRT.toString(),
                    imageUrlKTP = imageUriKTP.toString(),
                    imageUrlKK = imageUriKK.toString(),
                    imageUrlAkta = imageUriAkta.toString()
                )

                databaseReference.child(idPengajuan).setValue(pengajuan)
            }
        }.addOnFailureListener { exception ->
            // Penanganan kesalahan jika ada kesalahan saat mengunggah gambar
            showErrorDialog("Terjadi kesalahan saat mengunggah gambar: ${exception.localizedMessage}")
        }.addOnProgressListener { taskSnapshot ->
            // Handle progress events (if needed)
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            // Update progress UI or perform additional actions
        }
    }

    data class Pengajuan(
        val id: String = "",
        val id_pengaju: String = "",
        val tanggalPengajuan: String = "",
        val status: String = "",
        val surat: String = "",
        val jenisSurat: String = "",
        val imageUrlPengantarRT: String = "",
        val imageUrlKTP: String = "",
        val imageUrlKK: String = "",
        val imageUrlAkta: String = ""
    )

    private fun showSuccessDialog(message: String, idPengajuan: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("BERHASIL")
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { _, _ ->
            // Setelah menambahkan data, tampilkan notifikasi berhasil
            showToast("Data Pengajuan berhasil diunggah!")

            // Kembali ke MainActivity
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            when (requestCode) {
                // Pengantar RT
                PICK_IMAGE_REQUEST -> {
                    imageUriPengantarRT = data.data
                    b.ImvPKtp.setImageURI(imageUriPengantarRT)
                }
                // KTP
                PICK_IMAGE_REQUEST + 1 -> {
                    imageUriKTP = data.data
                    b.ImvKtpKtp.setImageURI(imageUriKTP)
                }
                // KK
                PICK_IMAGE_REQUEST + 2 -> {
                    imageUriKK = data.data
                    b.ImvKkKtp.setImageURI(imageUriKK)
                }
                // Akta
                PICK_IMAGE_REQUEST + 3 -> {
                    imageUriAkta = data.data
                    b.ImvAktaKtp.setImageURI(imageUriAkta)
                }
            }
        }
    }
}
