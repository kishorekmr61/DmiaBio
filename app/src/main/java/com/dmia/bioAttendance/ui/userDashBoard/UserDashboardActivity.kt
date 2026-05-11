package com.dmia.bioAttendance.ui.UserDashboardActivity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dmia.bioAttendance.R
import com.dmia.bioAttendance.data.constant.ApiConstant
import com.dmia.bioAttendance.data.helpers.AppPreference
import com.dmia.bioAttendance.databinding.ActivityUserDashboardBinding
import com.dmia.bioAttendance.helper.NetworkResult
import com.dmia.bioAttendance.model.DomainModel
import com.dmia.bioAttendance.model.UsersData
import com.dmia.bioAttendance.ui.empleyeeRegistration.EmployeeRegistrationActivity
import com.dmia.bioAttendance.ui.loginActivity.LoginActivity
import com.dmia.bioAttendance.ui.userAttendance.UserAttendanceActivity
import com.dmia.bioAttendance.ui.userDashBoard.UserDashboardViewModel
import com.dmia.bioAttendance.utils.PermissionsUtil
import com.dmia.bioAttendance.utils.isToday
import com.dmia.bioAttendance.utils.showDialog
import com.dmia.bioAttendance.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray

@AndroidEntryPoint
class UserDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDashboardBinding
    private val vm: UserDashboardViewModel by viewModels()


    val getdomains: ArrayList<DomainModel> = arrayListOf()
    val REQUEST_READ_PHONE_STATE = 110
    private val REQUEST_ACCESS_FINE_LOCATION: Int = 111
    val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private var isSettingsDialogOpened = false
    private var permissionAskingCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = vm
        setObserver()
        PermissionsUtil.askPermissions(this)
        PermissionsUtil.checkPermissions(this, *permissions)
        checkPermissions()
        binding.mainlayout?.setVisibility(View.VISIBLE)
        if (AppPreference.read(ApiConstant.EMPLOYEEDETAILS, "")?.isEmpty() == true) {
            binding.adminBtn?.visibility = View.GONE
            binding.registerTxt?.text = "Registration Request"
            binding.attendanceLyout.visibility = View.GONE
        } else {
            binding.adminBtn?.visibility = View.VISIBLE
            binding.checkinattendanceLayout.visibility = View.VISIBLE
            binding.checkoutattendanceLayout.visibility = View.VISIBLE
            var employeedetails = Gson().fromJson(
                AppPreference.read(ApiConstant.EMPLOYEEDETAILS, ""),
                UsersData::class.java
            )
            binding.registerTxt?.text = employeedetails.ID.plus(" ").plus(employeedetails.Name)
        }
        binding.registerLayout.setOnClickListener {
            if (AppPreference.read(ApiConstant.EMPLOYEEID, "")?.isEmpty() == true) {
                val intent =
                    Intent(getApplicationContext(), EmployeeRegistrationActivity::class.java)
                startActivity(intent)
            } else {
                showDialog(
                    this@UserDashboardActivity,
                    getString(R.string.warning),
                    getString(R.string.singleuser_msg)
                ) { dialog, which -> dialog.dismiss() }
            }
        }

        binding.checkinattendanceLayout.setOnClickListener {
            if (AppPreference.read(ApiConstant.EMPLOYEEID, "")?.isNotEmpty() == true) {
                val intent = Intent(this, UserAttendanceActivity::class.java)
                intent.putExtra("Checkin", "Checkin")
                startActivity(intent)
            } else {
                showDialog(
                    this@UserDashboardActivity,
                    getString(R.string.error),
                    getString(R.string.no_employee)
                ) { dialog, which -> dialog.dismiss() }
            }
        }
        binding.checkoutattendanceLayout.setOnClickListener {
            if (!AppPreference.read(ApiConstant.ATTENDANCECHECKIN, false)) {
                if (AppPreference.read(ApiConstant.EMPLOYEEID, "")?.isNotEmpty() == true) {
                    val intent = Intent(this, UserAttendanceActivity::class.java)
                    intent.putExtra("Checkout", "Checkout")
                    startActivity(intent)
                } else {
                    showDialog(
                        this@UserDashboardActivity,
                        getString(R.string.error),
                        getString(R.string.no_employee)
                    ) { dialog, which -> dialog.dismiss() }
                }
            } else {
                showToast("Please check-in  and try to checkout")
            }
        }
        binding.adminBtn?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("Login", "Login")
            startActivity(intent)
            //
        }
    }


    private fun updateButtonState() {
        val checkedIn = AppPreference.read(ApiConstant.ATTENDANCECHECKIN, false)
        val checkedOut = AppPreference.read(ApiConstant.ATTENDANCECHECKOUT, false)
        if (!checkedIn && !checkedOut) {
            vm.checkin.value = true
            vm.checkout.value = false
            vm.checkindatetime.value = ""
            vm.checkoutdatetime.value = ""
        } else if (!checkedIn && checkedOut) {
            vm.checkin.value = false
            vm.checkout.value = true
            vm.checkindatetime.value = AppPreference.read(ApiConstant.ATTENDANCECHECKINTIME, "")
            vm.checkoutdatetime.value = ""
        } else if (checkedIn && !checkedOut) {
            vm.checkin.value = true
            vm.checkout.value = false
            vm.checkindatetime.value = AppPreference.read(ApiConstant.ATTENDANCECHECKINTIME, "")
            vm.checkoutdatetime.value = AppPreference.read(ApiConstant.ATTENDANCECHECKOUTTIME, "")
        }
    }


    open fun checkPermissions() {
        try {
            val hasPermissionPhoneState =
                ContextCompat.checkSelfPermission(
                    getApplicationContext(),
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermissionPhoneState) {
                ActivityCompat.requestPermissions(
                    this@UserDashboardActivity,
                    arrayOf<String>(Manifest.permission.READ_PHONE_STATE),
                    REQUEST_READ_PHONE_STATE
                )
            }
            val hasPermissionLocation =
                ContextCompat.checkSelfPermission(
                    getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermissionLocation) {
                ActivityCompat.requestPermissions(
                    this@UserDashboardActivity,
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity()
        } else {
            finish()
        }
    }


    open fun showDialogNotCancelable(
        title: String,
        message: String,
        okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setCancelable(false)
            .create()
            .show()
    }

    private fun setObserver() {
        vm.domainresponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.pgBar.visibility = View.GONE
                    response.data.let { resposnes ->
                        if (response.data?.string() == "200") {

                            var getDisplayNames = ArrayList<String>()
                            var jsonArray = JSONArray(response.data?.string())
                            (0 until jsonArray.length()).forEach {
                                val db = jsonArray.getJSONObject(it).getString("db")
                                val domain = jsonArray.getJSONObject(it).getString("domain")
                                getdomains.add(DomainModel(db, domain));
                                getDisplayNames.add(db)
                            }
                            getDisplayNames.add(0, "Select Server");
//                            binding.CompanySpinner.setItemsArray(getDisplayNames)
                        } else {
                            showDialog(
                                this@UserDashboardActivity,
                                "Failure",
                                getString(R.string.somethingerror)
                            ) { dialog, which -> dialog.dismiss() }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    binding.pgBar.visibility = View.GONE
                    showDialog(
                        this@UserDashboardActivity,
                        "Failure",
                        getString(R.string.somethingerror)
                    ) { dialog, which -> dialog.dismiss() }
                }

                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                updateLocation()
            } else {
                if (permissionAskingCount > 1) {
                    locationPermissionSettingPopup(
                        positiveCallback = {
                            isSettingsDialogOpened = false
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri: Uri =
                                Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }

                    )
                } else {
                    requestPermissions()
                }

            }
        }

    fun locationPermissionSettingPopup(
        positiveCallback: () -> Unit
    ) {
        val dialog = Dialog(this@UserDashboardActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_location_permission_setting_popup)
        val tvPositive = dialog.findViewById(R.id.tvPositive) as AppCompatTextView
        tvPositive.setOnClickListener {
            dialog.dismiss()
            positiveCallback.invoke()
        }
        dialog.show()
    }

    private fun updateLocation() {
        if (isLocationEnabled()) {
            observeLocationUpdates()
        } else {
            //showLongToast("Turn on location")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun observeLocationUpdates() {
        vm.userLocation.observe(this) {

        }
    }

    private fun requestPermissions() {
        ++permissionAskingCount
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    override fun onResume() {
        super.onResume()
//        normalizeForNewDay()
        updateButtonState()

    }


//    private fun normalizeForNewDay() {
//        if (!isToday(AppPreference.read(ApiConstant.ATTENDANCECHECKINTIME, ""))) {
//            AppPreference.write(ApiConstant.ATTENDANCECHECKIN, false)
//            AppPreference.write(ApiConstant.ATTENDANCECHECKOUT, false)
//            AppPreference.write(ApiConstant.ATTENDANCECHECKINTIME, "")
//            AppPreference.write(ApiConstant.ATTENDANCECHECKOUTTIME, "")
//            AppPreference.write(ApiConstant.ATTENDANCECHECKINTIME, "")
//        }
//    }

}