package com.dmia.bioAttendance.ui.userDashBoard.applyAttendance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dmia.bioAttendance.R
import com.dmia.bioAttendance.databinding.FragmentApplyAttendanceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplyAttendanceFragment : Fragment() {


     private lateinit var binding: FragmentApplyAttendanceBinding
    private val applyAttendanceViewModel: ApplyAttendanceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplyAttendanceBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.applyAttendanceViewModel = applyAttendanceViewModel
        return binding.root
    }



}