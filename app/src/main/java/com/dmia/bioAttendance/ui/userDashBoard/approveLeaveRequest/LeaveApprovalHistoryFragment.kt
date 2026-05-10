package com.dmia.bioAttendance.ui.userDashBoard.approveLeaveRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dmia.bioAttendance.databinding.FragmentLeaveApprovalHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaveApprovalHistoryFragment : Fragment() {

    private lateinit var binding: FragmentLeaveApprovalHistoryBinding
    private val leavehistoryViewModel: LeaveApprovalHistoryViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaveApprovalHistoryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.leavehistoryViewModel = leavehistoryViewModel
        return binding.root
    }


}