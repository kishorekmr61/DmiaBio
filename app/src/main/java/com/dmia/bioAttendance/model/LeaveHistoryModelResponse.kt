package com.dmia.bioAttendance.model

import com.customer.bms.utils.resource.WidgetViewModel
import com.dmia.bioAttendance.R

class LeaveHistoryModelResponse(
    var leaveID: String = "",
    var Name: String? = "",
    var Applyon: String? = "",
    var startDate: String? = "",
    var endDate: String? = "",
    var reason: String? = "",
    var leavetype: String? = "",
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.leave_history_row
    }
}