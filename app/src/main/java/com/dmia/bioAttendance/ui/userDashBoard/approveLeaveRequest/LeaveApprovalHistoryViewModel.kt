package com.dmia.bioAttendance.ui.userDashBoard.approveLeaveRequest

import android.app.Application
import androidx.lifecycle.ViewModel
import com.dmia.bioAttendance.repositry.UsersRepositry
import com.dmia.bioAttendance.utils.helper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaveApprovalHistoryViewModel @Inject constructor(
    var app: Application,
) : ViewModel() {
    // TODO: Implement the ViewModel
}