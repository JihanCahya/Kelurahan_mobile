package com.polinema.uas.sipkburengan
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.uas.sipkburengan.databinding.ActivityPengajuanKeteranganBinding
class PengajuanKeteranganActivity : AppCompatActivity() {
    lateinit var b: ActivityPengajuanKeteranganBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUriPengantarRT: Uri? = null
    private var imageUriKTP: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPengajuanKeteranganBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Inisialisasi DatabaseReference ke Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Pengajuan_keterangan")

        // Inisialisasi StorageReference ke Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        val btnSimpan = b.UpKeterangan

        // Tombol untuk memilih gambar Pengantar RT
        val btnPilihGambarRT = b.UpPKeterangan
        btnPilihGambarRT.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/pengajuan_keterangan/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Tombol untuk memilih gambar KTP
        val btnPilihGambarKTP = b.upKtpKeterangan
        btnPilihGambarKTP.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/pengajuan_keterangan/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST + 1) // Change request code
        }

        btnSimpan.setOnClickListener {
            if (imageUriPengantarRT != null && imageUriKTP != null) {
                // Generate unique ID for each entry
                val idPengajuan = databaseReference.push().key

                // Upload gambar Pengantar RT
                uploadImage(idPengajuan!!, "pengantar_rt", imageUriPengantarRT!!)

                // Upload gambar KTP
                uploadImage(idPengajuan, "ktp", imageUriKTP!!)
            } else {
                // Jika salah satu atau kedua gambar tidak dipilih, tampilkan pesan kesalahan
                showErrorDialog("Pilih gambar Pengantar RT dan KTP terlebih dahulu")
            }
        }
    }
    private fun uploadImage(idPengajuan: String, imageType: String, imageUri: Uri) {
        val timestamp = System.currentTimeMillis() // timestamp untuk menyertakan waktu unik

        // Format nama file dengan menambahkan timestamp dan jenis file ke dalamnya
        val fileName = "$idPengajuan-$imageType-$timestamp.jpg"

        val imageRef = storageReference.child("images/pengajuan_keterangan/$fileName")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()

                // Setelah mendapatkan URL gambar yang benar, simpan data ke Firebase Database
                val pengajuan = Pengajuan(idPengajuan, imageUrl)
                databaseReference.child(idPengajuan).setValue(pengajuan)

                // Tampilkan pesan sukses
                showSuccessDialog("Data Pengajuan KK berhasil diunggah!")
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
        val imageUrl: String = ""
    )

    private fun showSuccessDialog(message: String) {
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
                    b.ImvPKeterangan.setImageURI(imageUriPengantarRT)
                }
                // KTP
                PICK_IMAGE_REQUEST + 1 -> {
                    imageUriKTP = data.data
                    b.ImvKtpKeterangan.setImageURI(imageUriKTP)
                }
            }
        }
    }
}
