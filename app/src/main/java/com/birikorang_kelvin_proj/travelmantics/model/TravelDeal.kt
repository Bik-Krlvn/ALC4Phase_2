package com.birikorang_kelvin_proj.travelmantics.model

import java.io.Serializable

data class TravelDeal(
    val tile: String,
    val description: String,
    val price: String,
    val imageUrl: String?,
    var imageName: String?
) : Serializable {
    var id: String? = null
}