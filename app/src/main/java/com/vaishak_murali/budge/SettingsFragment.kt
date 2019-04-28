package com.vaishak_murali.budge


import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // declaring and initializing firebaseAuthInstance
        val firebaseAuthInstance = FirebaseAuth.getInstance()

        // Displaying user name and mobile number
        dpName.text = firebaseAuthInstance.currentUser!!.displayName
        dpMobile.text = firebaseAuthInstance.currentUser!!.phoneNumber

        // Setting user profile picture
        Glide
            .with(this)
            .load(firebaseAuthInstance.currentUser!!.photoUrl)
            .centerCrop()
            .into(dpImage)

        //
    }


}
