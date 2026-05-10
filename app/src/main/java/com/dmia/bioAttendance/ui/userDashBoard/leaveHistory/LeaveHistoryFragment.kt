package com.dmia.bioAttendance.ui.userDashBoard.leaveHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dmia.bioAttendance.databinding.FragmentLeaveHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaveHistoryFragment : Fragment() {

    private lateinit var binding: FragmentLeaveHistoryBinding
    private val leavehistoryViewModel: LeaveHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaveHistoryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.leavehistoryViewModel = leavehistoryViewModel
        return binding.root
    }

}