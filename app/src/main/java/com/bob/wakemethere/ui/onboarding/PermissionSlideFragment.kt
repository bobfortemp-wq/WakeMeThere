package com.bob.wakemethere.ui.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bob.wakemethere.R
import com.bob.wakemethere.databinding.FragmentPermissionSlideBinding

class PermissionSlideFragment : Fragment() {

    private var _binding: FragmentPermissionSlideBinding? = null
    private val binding get() = _binding!!

    private var permissionType: PermissionType = PermissionType.FINE_LOCATION

    private val fineLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            Toast.makeText(requireContext(), "Location permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
        updateUiState()
    }

    private val backgroundLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Background location permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Background location permission denied", Toast.LENGTH_SHORT).show()
        }
        updateUiState()
    }

    private val notificationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Notification permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
        updateUiState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val typeOrdinal = arguments?.getInt(ARG_PERMISSION_TYPE, 0) ?: 0
        permissionType = PermissionType.entries.getOrElse(typeOrdinal) { PermissionType.FINE_LOCATION }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionSlideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSlideContent()
        binding.btnGrantPermission.setOnClickListener {
            requestSlidePermission()
        }
    }

    override fun onResume() {
        super.onResume()
        updateUiState()
    }

    private fun setupSlideContent() {
        when (permissionType) {
            PermissionType.FINE_LOCATION -> {
                binding.imgPermissionIcon.setImageResource(R.drawable.ic_location_on)
                binding.tvTitle.setText(R.string.title_fine_location)
                binding.tvDescription.setText(R.string.desc_fine_location)
            }
            PermissionType.BACKGROUND_LOCATION -> {
                binding.imgPermissionIcon.setImageResource(R.drawable.ic_background_location)
                binding.tvTitle.setText(R.string.title_background_location)
                binding.tvDescription.setText(R.string.desc_background_location)
            }
            PermissionType.POST_NOTIFICATIONS -> {
                binding.imgPermissionIcon.setImageResource(R.drawable.ic_notifications)
                binding.tvTitle.setText(R.string.title_notifications)
                binding.tvDescription.setText(R.string.desc_notifications)
            }
        }
    }

    private fun requestSlidePermission() {
        val context = requireContext()
        when (permissionType) {
            PermissionType.FINE_LOCATION -> {
                fineLocationLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            PermissionType.BACKGROUND_LOCATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    } else {
                        Toast.makeText(
                            context,
                            "Please grant foreground location permission first.",
                            Toast.LENGTH_LONG
                        ).show()
                        fineLocationLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            }
            PermissionType.POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun updateUiState() {
        val context = context ?: return
        val isGranted = isCurrentPermissionGranted(context)

        if (isGranted) {
            binding.tvStatus.text = getString(R.string.status_granted)
            binding.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            binding.btnGrantPermission.text = getString(R.string.btn_permission_granted)
            binding.btnGrantPermission.isEnabled = false
        } else {
            binding.tvStatus.text = getString(R.string.status_required)
            binding.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            binding.btnGrantPermission.text = getString(R.string.btn_allow_permission)
            binding.btnGrantPermission.isEnabled = true
        }
    }

    private fun isCurrentPermissionGranted(context: Context): Boolean {
        return when (permissionType) {
            PermissionType.FINE_LOCATION -> {
                isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                        isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            PermissionType.BACKGROUND_LOCATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    isPermissionGranted(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    true
                }
            }
            PermissionType.POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    true
                }
            }
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PERMISSION_TYPE = "arg_permission_type"

        fun newInstance(permissionType: PermissionType): PermissionSlideFragment {
            val fragment = PermissionSlideFragment()
            val args = Bundle().apply {
                putInt(ARG_PERMISSION_TYPE, permissionType.ordinal)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
