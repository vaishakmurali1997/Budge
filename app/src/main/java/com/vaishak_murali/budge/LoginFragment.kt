package com.vaishak_murali.budge


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    // initialization
    private val RC_SIGN_IN = 1
    private lateinit var database:DatabaseReference
    private lateinit var auth:FirebaseAuth
    private lateinit var googleApiClient:GoogleApiClient
    private lateinit var authListener:FirebaseAuth.AuthStateListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GSO config
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("598525156672-l5nbgn4bp37s666ilahlfb9gpufjpp13.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(activity!!.applicationContext!!)
            .enableAutoManage(activity!!) {
                Toast.makeText(activity!!, "Error 001:  while Signing you in.", Toast.LENGTH_LONG)
                    .show()
            }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        auth = FirebaseAuth.getInstance()
        authListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null) {
                Log.d("Signed In","User connected!")

                database = FirebaseDatabase.getInstance().reference
                database.child("Users").addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        // Check if the user is already a member
                        if (p0.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                            // Navigate to homeFragment
                            Navigation.findNavController(googleSignInBtn!!)
                                .navigate(R.id.action_loginFragment_to_homeFragment)
                        }else{
                            // Navigate to settingsFragment
                            Navigation.findNavController(googleSignInBtn!!)
                                .navigate(R.id.action_loginFragment_to_settingsFragment)
                        }
                    }

                })


            }
        }

        // Google SignIn Button
        googleSignInBtn.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if the user is connected
        auth.addAuthStateListener(authListener)
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Error 004", "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("Account_ID", "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener{  task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Success_Msg", "signInWithCredential:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Failure_Msg", "signInWithCredential:failure", task.exception)
                    Toast.makeText(activity!!.applicationContext, "Error3: SignIn FAILED!", Toast.LENGTH_SHORT)
                        .show()
                }

                // ...
            }
    }

}



