package com.dmia.bioAttendance.ui.userDashBoard.applyAttendance

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.dmia.bioAttendance.repositry.UsersRepositry
import com.dmia.bioAttendance.utils.helper.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ApplyAttendanceViewModel @Inject constructor(
    var app: Application,
) : ViewModel() {

    var isloading = ObservableField(false)
}