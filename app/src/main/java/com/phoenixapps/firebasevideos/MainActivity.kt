package com.phoenixapps.firebasevideos

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.phoenixapps.firebasevideos.fragments.DownloadFragment
import com.phoenixapps.firebasevideos.fragments.UploadFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mUploadFragment: Fragment
    private lateinit var mDownloadFragment: Fragment

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_upload -> {
                changeFragment(mUploadFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_download -> {
                changeFragment(mDownloadFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mUploadFragment = UploadFragment()
        mDownloadFragment = DownloadFragment()

        changeFragment(mUploadFragment)

        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
