package com.dmia.bioAttendance.model

import com.google.gson.annotations.SerializedName

data class PostCheckinCheckOut(
    @SerializedName("DtStamp")
    val dtstamp: String?,
    @SerializedName("EmpId")
    val empID: String?,
    @SerializedName("Lat")
    val latitude: String?,
    @SerializedName("Lng")
    val longitude: String?,
    @SerializedName("PunchType")
    val PunchType: String?

)
