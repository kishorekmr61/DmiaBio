package com.dmia.bioAttendance.model

data class CommonModelResponse(
    var Description : String,
    var Status : String,
    var EmpName : String,
)

data class AtendanceinoutResponse(
    var message : String,
    var success : String,
)
