package com.birikorang_kelvin_proj.travelmantics.ui.travelmatics

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.birikorang_kelvin_proj.travelmantics.R
import com.birikorang_kelvin_proj.travelmantics.adapter.DealAdapter
import com.birikorang_kelvin_proj.travelmantics.common.ItemCallBack
import com.birikorang_kelvin_proj.travelmantics.common.utils.FirebaseUtil
import com.birikorang_kelvin_proj.travelmantics.model.TravelDeal
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class DealsActivity : AppCompatActivity(),ItemCallBack<TravelDeal> {
    override fun onClick(data: TravelDeal?) {
        val intent = Intent(this, AddOrUpdateDealActivity::class.java)
        intent.putExtra("Deal", data)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showMenu() {
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_activity_menu, menu)
        val insertMenu = menu?.findItem(R.id.insert_menu)
        insertMenu?.isVisible = FirebaseUtil.isAdmin == true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.insert_menu -> {
                val intent = Intent(this, AddOrUpdateDealActivity::class.java)
                startActivity(intent)
            }
            R.id.logout_menu -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        Timber.i("User Logged Out")
                        FirebaseUtil.attachListener()
                    }
                FirebaseUtil.detachListener()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener()
    }

    override fun onResume() {
        super.onResume()
        FirebaseUtil.openFbReference("traveldeals", this)
        val adapter = DealAdapter()
        val rvDeals: RecyclerView = rvDeals
        val dealsLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter.addData()
        adapter.setItemCallBack(this)
        rvDeals.adapter = adapter
        rvDeals.layoutManager = dealsLayoutManager
        FirebaseUtil.attachListener()
    }
}
