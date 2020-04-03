package com.phoenixapps.firebasevideos.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.download_fragment.*
import com.phoenixapps.firebasevideos.R

class DownloadFragment: Fragment() {
    private lateinit var mFirebaseFirestore: FirebaseFirestore
    private lateinit var mFirebaseStorageRef: StorageReference
    private lateinit var mDialog: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.download_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFirebaseFirestore = FirebaseFirestore.getInstance()
        mFirebaseStorageRef = FirebaseStorage.getInstance().getReference("Images")

        mDialog = Dialog(this.context!!)
        mDialog.setCancelable(false)
        mDialog.setContentView(R.layout.loading_dialog)

        download_btn.setOnClickListener { loadVideo(it) }
    }

    private fun loadVideo(view: View) {
        showLoading()

        FirebaseFirestore.getInstance()
            .collection("Videos")
            .document("1")
            .get()
            .addOnSuccessListener {
                hideLoading()
                showVideo(it.get("url").toString())
            }
            .addOnFailureListener {
                hideLoading()
                Log.e("Download", "Firestore", it)
            }
    }

    private fun showVideo(path: String) {
        download_vw.visibility = View.VISIBLE

        download_vw.setVideoPath(path)

        val mediaController = MediaController(this.context)
        mediaController.setAnchorView(download_vw)
        download_vw.setMediaController(mediaController)

        download_vw.start()
    }

    private fun showLoading() {
        mDialog.show()
    }

    private fun hideLoading() {
        mDialog.dismiss()
    }
}