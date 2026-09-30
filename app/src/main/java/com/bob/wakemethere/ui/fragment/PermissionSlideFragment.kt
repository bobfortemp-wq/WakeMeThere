package com.bob.wakemethere.ui.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bob.wakemethere.R
import com.bob.wakemethere.data.model.PermissionType
import com.bob.wakemethere.databinding.FragmentPermissionSlideBinding
import com.bob.wakemethere.ui.viewmodel.PermissionSlideViewModel
import kotlinx.coroutines.launch

class PermissionSlideFragment : Fragment() {

    private var _binding: FragmentPermissionSlideBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PermissionSlideViewModel by viewModels()

    private var permissionType: PermissionType = PermissionType.FINE_LOCATION

    private val fineLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            Toast.makeText(requireContext(), "Location permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
        viewModel.checkPermissionStatus(requireContext())
    }

    private val backgroundLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Background location permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Background location permission denied", Toast.LENGTH_SHORT).show()
        }
        viewModel.checkPermissionStatus(requireContext())
    }

    private val notificationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Notification permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
        viewModel.checkPermissionStatus(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val typeOrdinal = arguments?.getInt(ARG_PERMISSION_TYPE, 0) ?: 0
        permissionType = PermissionType.entries.getOrElse(typeOrdinal) { PermissionType.FINE_LOCATION }
        viewModel.initPermissionType(permissionType)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPermissionSlideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSlideContent()

        binding.card1.setOnClickListener {
            requestSlidePermission()
        }
        binding.switchCard1.setOnClickListener {
            requestSlidePermission()
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPermissionStatus(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (binding.switchCard1.isVisible) {
                        binding.switchCard1.isChecked = uiState.isGranted
                    }
                }
            }
        }
    }

    private fun setupSlideContent() {
        when (permissionType) {
            PermissionType.FINE_LOCATION -> {
                binding.ivHeroGraphic.setImageResource(R.drawable.hero_slide_1)
                binding.tvStepTag.setText(R.string.step01_tag)
                binding.tvTitle.setText(R.string.title_slide1)
                binding.tvDescription.setText(R.string.desc_slide1)

                binding.ivCard1Icon.setImageResource(R.drawable.ic_radar)
                binding.tvCard1Title.setText(R.string.card1_slide1_title)
                binding.tvCard1Sub.setText(R.string.card1_slide1_sub)
                binding.tvCard1Badge.setText(R.string.card1_slide1_badge)
                binding.tvCard1Badge.visibility = View.VISIBLE
                binding.switchCard1.visibility = View.GONE

                binding.ivCard2Icon.setImageResource(R.drawable.ic_shield_check)
                binding.tvCard2Title.setText(R.string.card2_slide1_title)
                binding.tvCard2Sub.setText(R.string.card2_slide1_sub)
                binding.tvCard2Badge.setText(R.string.card2_slide1_badge)
                binding.tvCard2Badge.visibility = View.VISIBLE
                binding.tvCard2Value.visibility = View.GONE
            }
            PermissionType.BACKGROUND_LOCATION -> {
                binding.ivHeroGraphic.setImageResource(R.drawable.hero_slide_2)
                binding.tvStepTag.setText(R.string.step02_tag)
                binding.tvTitle.setText(R.string.title_slide2)
                binding.tvDescription.setText(R.string.desc_slide2)

                binding.ivCard1Icon.setImageResource(R.drawable.ic_location_pin)
                binding.tvCard1Title.setText(R.string.card1_slide2_title)
                binding.tvCard1Sub.setText(R.string.card1_slide2_sub)
                binding.tvCard1Badge.visibility = View.GONE
                binding.switchCard1.visibility = View.VISIBLE

                binding.ivCard2Icon.setImageResource(R.drawable.ic_battery)
                binding.tvCard2Title.setText(R.string.card2_slide2_title)
                binding.tvCard2Sub.visibility = View.GONE
                binding.tvCard2Badge.visibility = View.GONE
                binding.tvCard2Value.setText(R.string.card2_slide2_val)
                binding.tvCard2Value.visibility = View.VISIBLE
            }
            PermissionType.POST_NOTIFICATIONS -> {
                binding.ivHeroGraphic.setImageResource(R.drawable.hero_slide_3)
                binding.tvStepTag.setText(R.string.step03_tag)
                binding.tvTitle.setText(R.string.title_slide3)
                binding.tvDescription.setText(R.string.desc_slide3)

                binding.ivCard1Icon.setImageResource(R.drawable.ic_clock_alarm)
                binding.tvCard1Title.setText(R.string.card1_slide3_title)
                binding.tvCard1Sub.setText(R.string.card1_slide3_sub)
                binding.tvCard1Badge.visibility = View.GONE
                binding.switchCard1.visibility = View.VISIBLE

                binding.ivCard2Icon.setImageResource(R.drawable.ic_shield_check)
                binding.tvCard2Title.setText(R.string.card2_slide3_title)
                binding.tvCard2Sub.visibility = View.GONE
                binding.tvCard2Badge.setText(R.string.card2_slide3_badge)
                binding.tvCard2Badge.visibility = View.VISIBLE
                binding.tvCard2Value.visibility = View.GONE
            }
        }
    }

    fun requestSlidePermission() {
        if (context == null) return
        when (permissionType) {
            PermissionType.FINE_LOCATION -> {
                fineLocationLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                )
            }
            PermissionType.BACKGROUND_LOCATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            }
            PermissionType.POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
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
