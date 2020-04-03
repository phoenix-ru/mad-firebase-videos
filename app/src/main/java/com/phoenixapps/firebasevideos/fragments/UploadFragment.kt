package com.phoenixapps.firebasevideos.fragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.upload_fragment.*
import android.webkit.MimeTypeMap
import com.google.firebase.firestore.CollectionReference
import com.phoenixapps.firebasevideos.R


class UploadFragment : Fragment() {
    private var mUri: Uri? = null
    private lateinit var mFirebaseFirestoreRef: CollectionReference
    private lateinit var mFirebaseStorageRef: StorageReference
    private lateinit var mDialog: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.upload_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFirebaseFirestoreRef = FirebaseFirestore.getInstance().collection("Videos")
        mFirebaseStorageRef = FirebaseStorage.getInstance().getReference("Videos")

        mDialog = Dialog(this.context!!)
        mDialog.setCancelable(false)
        mDialog.setContentView(R.layout.loading_dialog)

        select_btn.setOnClickListener { selectVideo(it) }
        upload_btn.setOnClickListener { uploadVideo(it) }
    }

    private fun selectVideo(view: View) {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    private fun uploadVideo(view: View) {
        if (mUri == null) {
            Toast.makeText(this.context, "Please, select a video", Toast.LENGTH_LONG).show()
            return
        }

        showLoading()

        val videoRef = mFirebaseStorageRef.child(
            "1." + getExtension(mUri!!)
        )

        videoRef.putFile(mUri!!)
            .continueWithTask {
                if (!it.isSuccessful) {
                    hideLoading()
                    throw it.exception!!
                }

                videoRef.downloadUrl
            }
            .addOnSuccessListener {
                mFirebaseFirestoreRef
                    .document("1")
                    .set(hashMapOf("url" to it.toString()))
                    .addOnSuccessListener {
                        hideLoading()

                        Toast.makeText(
                            this.context,
                            "Successfully added to Firestore",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                    .addOnFailureListener { e ->
                        hideLoading()
                        Log.e("Upload", "Firestore", e)
                    }
            }
            .addOnFailureListener {
                hideLoading()
                Log.e("Upload", "Storage", it)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            upload_vw.visibility = VISIBLE

            upload_vw.setVideoURI(data.data)

            val mediaController = MediaController(upload_vw.context)
            mediaController.setAnchorView(upload_vw)
            upload_vw.setMediaController(mediaController)

//            upload_vw.start()

            mUri = data.data
        } else {
            upload_vw.visibility = GONE
        }
    }

    private fun getExtension(uri: Uri): String? {
        try {
            val contentResolver = this.activity?.contentResolver
            val objectMimeTypeMap = MimeTypeMap.getSingleton()

            return objectMimeTypeMap.getExtensionFromMimeType(
                contentResolver?.getType(uri)
            )
        } catch (e: Exception) {
            Toast.makeText(this.context, "getExtension: " + e.message, Toast.LENGTH_SHORT).show()
        }

        return null
    }

    private fun showLoading() {
        mDialog.show()
    }

    private fun hideLoading() {
        mDialog.dismiss()
    }
}