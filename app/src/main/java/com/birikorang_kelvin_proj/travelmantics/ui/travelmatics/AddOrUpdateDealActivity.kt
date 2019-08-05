package com.birikorang_kelvin_proj.travelmantics.ui.travelmatics

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.birikorang_kelvin_proj.travelmantics.R
import com.birikorang_kelvin_proj.travelmantics.common.utils.FirebaseUtil
import com.birikorang_kelvin_proj.travelmantics.common.utils.GlideApp
import com.birikorang_kelvin_proj.travelmantics.model.TravelDeal
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_or_update_deal.*
import org.jetbrains.anko.toast
import timber.log.Timber

class AddOrUpdateDealActivity : AppCompatActivity() {
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    private val PICTURE_RESULT = 42
    private var deal: TravelDeal? = null
    private var loadingProgressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_update_deal)
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        loadingProgressBar = progressBar
        loadingProgressBar?.visibility = View.GONE
        if (FirebaseUtil.isAdmin) {
            btnImage.visibility = View.VISIBLE
        } else {
            btnImage.visibility = View.GONE
        }
        val intent = intent
        var dealExtras = intent.getSerializableExtra("Deal") as TravelDeal?
        if (dealExtras == null) {
            dealExtras = TravelDeal()
        }
        this.deal = dealExtras
        txtTitle.setText(deal?.tile)
        txtDescription.setText(deal?.description)
        txtPrice.setText(deal?.price)
        showImage(deal?.imageUrl)

        btnImage.setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickImageIntent.type = "image/jpeg"
            pickImageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(pickImageIntent, "Insert Picture"), PICTURE_RESULT)
        }
    }

    private fun saveDeal() {
        deal?.tile = txtTitle.text.toString()
        deal?.description = txtDescription.text.toString()
        deal?.price = txtPrice.text.toString()
        if (deal?.id == null) {
            mDatabaseReference?.push()?.setValue(deal)
        } else {
            mDatabaseReference?.child(deal?.id!!)?.setValue(deal)
        }
    }

    private fun deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show()
            return
        }
        deal?.id?.let { mDatabaseReference?.child(it)?.removeValue() }
        Timber.i(deal?.imageName)
        if (deal?.imageName != null && !deal?.imageName.isNullOrBlank()) {
            val picRef = FirebaseUtil.mStorage?.reference?.child(deal?.imageName!!)
            picRef?.delete()?.addOnSuccessListener { Timber.i("Image Successfully Deleted") }
                ?.addOnFailureListener { e -> Timber.i(e.message, "Delete Image") }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun backToList() {
        onBackPressed()
    }

    private fun clean() {
        txtTitle.setText("")
        txtPrice.setText("")
        txtDescription.setText("")
        txtTitle.requestFocus()
    }

    private fun enableEditTexts(isEnabled: Boolean) {
        txtTitle.isEnabled = isEnabled
        txtDescription.isEnabled = isEnabled
        txtPrice.isEnabled = isEnabled
    }

    private fun showImage(url: String?) {
        if (url != null && url.isNotEmpty()) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            GlideApp.with(this)
                .load(url)
                .override(width, width * 2 / 3)
                .centerCrop()
                .into(image)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.save_menu -> {
                saveDeal()
                toast("Deal saved")
                clean()
                backToList()
            }
            R.id.delete_menu -> {
                deleteDeal()
                toast("Deal Deleted")
                backToList()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        if (FirebaseUtil.isAdmin) {
            menu?.findItem(R.id.delete_menu)?.isVisible = true
            menu?.findItem(R.id.save_menu)?.isVisible = true
            enableEditTexts(true)
        } else {
            menu?.findItem(R.id.delete_menu)?.isVisible = false
            menu?.findItem(R.id.save_menu)?.isVisible = false
            enableEditTexts(false)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK) {
            loadingProgressBar?.visibility = View.VISIBLE
            val imageUri = data?.data
            val ref = imageUri?.lastPathSegment?.let { FirebaseUtil.mStorageRef?.child(it) }
            ref?.putFile(imageUri)?.addOnProgressListener { snap ->
                val progress = (100 * snap.bytesTransferred / snap.totalByteCount)
                loadingProgressBar?.progress = progress.toInt()
            }?.addOnSuccessListener { task ->
                deal?.imageName = task.storage.path
                ref.downloadUrl.addOnCompleteListener {
                    deal?.imageUrl = it.result.toString()
                    showImage(it.result.toString())
                    loadingProgressBar?.progress = 0
                    loadingProgressBar?.visibility = View.GONE
                }
            }

        }
    }
}
