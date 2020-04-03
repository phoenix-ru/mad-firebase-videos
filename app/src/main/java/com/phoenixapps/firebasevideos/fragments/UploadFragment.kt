package com.phoenixapps.firebasevideos.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.phoenixapps.firebasevideos.R
import kotlinx.android.synthetic.main.upload_fragment.*

class UploadFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.upload_fragment, container, false)
    }

    fun selectVideo(view: View) {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    fun uploadVideo(view: View) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            upload_vw.setVideoURI(data.data)

            val mediaController = MediaController(this.context)
            mediaController.setAnchorView(upload_vw)
            upload_vw.setMediaController(mediaController)

            upload_vw.start()
        }
    }
}