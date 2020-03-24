package com.ngo.listeners

interface OnMarkStatusClickListener {
    fun onMarkClick(forwardId:String,status:String,comment:String)
    fun onLocationClick(lat:String,lng:String)
    fun onPhotoClick(complaintId:Int)
    fun onDescPhotoClick(url:String?)

}