package com.birikorang_kelvin_proj.travelmantics.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.birikorang_kelvin_proj.travelmantics.R
import com.birikorang_kelvin_proj.travelmantics.common.ItemCallBack
import com.birikorang_kelvin_proj.travelmantics.common.utils.FirebaseUtil
import com.birikorang_kelvin_proj.travelmantics.common.utils.GlideApp
import com.birikorang_kelvin_proj.travelmantics.model.TravelDeal
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.deal_list.view.*


class DealAdapter : RecyclerView.Adapter<DealVH>() {
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mChildListener: ChildEventListener? = null
    private var deals: ArrayList<TravelDeal?>? = null
    private var itemCallBack: ItemCallBack<TravelDeal>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealVH {
        return DealVH(LayoutInflater.from(parent.context).inflate(R.layout.deal_list, parent, false))
    }

    override fun getItemCount() = deals?.size ?: 0


    override fun onBindViewHolder(holder: DealVH, position: Int) {
        val data = deals?.get(position)
        holder.bind(data, itemCallBack)
    }

    fun setItemCallBack(callBack: ItemCallBack<TravelDeal>) {
        itemCallBack = callBack
    }

    fun addData() {
        val data = ArrayList<TravelDeal?>()
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        mChildListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val d = p0.getValue(TravelDeal::class.java)
                d?.id = p0.key
                data.add(d)
                deals = data
                notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }
        mDatabaseReference?.addChildEventListener(mChildListener!!)
    }
}

class DealVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
        data: TravelDeal?,
        itemCallBack: ItemCallBack<TravelDeal>?
    ) {
        itemView.tvDescription.text = data?.description
        itemView.tvPrice.text = data?.price
        itemView.tvTitle.text = data?.tile
        itemView.setOnClickListener { itemCallBack?.onClick(data) }
        GlideApp.with(itemView.context)
            .load(data?.imageUrl)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(itemView.imageDeal)
    }
}