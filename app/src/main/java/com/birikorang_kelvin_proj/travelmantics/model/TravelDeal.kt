package com.birikorang_kelvin_proj.travelmantics.model

import java.io.Serializable

data class TravelDeal(
    var tile: String,
    var description: String,
    var price: String,
    var imageUrl: String?,
    var imageName: String?
)  : Serializable {
    var id: String? = null
    constructor():this("","","","","")
}