package com.birikorang_kelvin_proj.travelmantics.common.utils

import com.birikorang_kelvin_proj.travelmantics.ui.travelmatics.DealsActivity
import com.birikorang_kelvin_proj.travelmantics.model.TravelDeal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList


object FirebaseUtil {
    var mFirebaseDatabase: FirebaseDatabase? = null
    var mDatabaseReference: DatabaseReference? = null
    var mFirebaseAuth: FirebaseAuth? = null
    var mStorage: FirebaseStorage? = null
    var mStorageRef: StorageReference? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val RC_SIGN_IN = 123
    var isAdmin: Boolean = false

    fun openFbReference(ref: String, callerActivity: DealsActivity) {
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                signIn(callerActivity)
            }else{
                checkAdmin(it.uid, callerActivity)
            }
        }
        connectStorage()
        mDatabaseReference = mFirebaseDatabase?.reference?.child(ref)
    }

    private fun connectStorage() {
        mStorage = FirebaseStorage.getInstance()
        mStorageRef = mStorage?.reference?.child("deals_pictures")
    }

    private fun checkAdmin(
        uid: String?,
        callerActivity: DealsActivity
    ) {
        isAdmin = false
        val dbRef = uid?.let { mFirebaseDatabase?.reference?.child("administrators")?.child(it) }
        val listener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                isAdmin = true
                callerActivity.showMenu()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }
        dbRef?.addChildEventListener(listener)
    }

    private fun signIn(callerActivity: DealsActivity) {
        val providers = asList(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        callerActivity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    fun attachListener() {
        mAuthListener?.let { mFirebaseAuth?.addAuthStateListener(it) }
    }

    fun detachListener() {
        mAuthListener?.let { mFirebaseAuth?.removeAuthStateListener(it) }
    }
}