package hu.bme.aut.android.myshoppinglist

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import hu.bme.aut.android.myshoppinglist.data.ShoppingList
import hu.bme.aut.android.myshoppinglist.extension.validateNonEmpty
import kotlinx.android.synthetic.main.activity_create_new_list.*
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.*

class CreateNewListActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_list)

        btnSend.setOnClickListener { sendClick() }
        btnAttach.setOnClickListener { attachClick() }
    }

    private fun sendClick() {
        if (!validateForm()) {
            return
        }

        if (imgAttach.visibility != View.VISIBLE) {
            uploadPost()
        } else {
            try {
                uploadPostWithImage()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun validateForm() = etTitle.validateNonEmpty() && etBody.validateNonEmpty()

    private fun uploadPost(imageUrl: String? = null) {
        val key = FirebaseDatabase.getInstance().reference.child("posts").push().key ?: return
        val newPost = ShoppingList(uid, userName, etTitle.text.toString())

        FirebaseDatabase.getInstance().reference
            .child("posts")
            .child(key)
            .setValue(newPost)
            .addOnCompleteListener {
                toast("Post created")
                finish()
            }
    }

    private fun attachClick() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap ?: return
            imgAttach.setImageBitmap(imageBitmap)
            imgAttach.visibility = View.VISIBLE
        }
    }

    private fun uploadPostWithImage() {
        val bitmap: Bitmap = (imgAttach.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInBytes = baos.toByteArray()

        val storageReference = FirebaseStorage.getInstance().reference
        val newImageName = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg"
        val newImageRef = storageReference.child("images/$newImageName")

        newImageRef.putBytes(imageInBytes)
            .addOnFailureListener { exception ->
                toast(exception.message)
            }
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }

                newImageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                uploadPost(downloadUri.toString())
            }
    }
}
