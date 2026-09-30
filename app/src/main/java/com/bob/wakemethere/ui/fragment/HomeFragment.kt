package com.bob.wakemethere.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bob.wakemethere.R
import com.bob.wakemethere.databinding.FragmentHomeBinding
import com.bob.wakemethere.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private var myLocationOverlay: MyLocationNewOverlay? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        setupListeners()
        observeViewModel()
    }

    private fun setupMap() {
        val appContext = requireContext().applicationContext
        Configuration.getInstance().userAgentValue = appContext.packageName

        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)

        val mapController = binding.mapView.controller
        mapController.setZoom(14.5)

        // Center on Grand Central Terminal area (NYC)
        val grandCentral = GeoPoint(40.7527, -73.9772)
        mapController.setCenter(grandCentral)

        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(appContext), binding.mapView).apply {
            enableMyLocation()
        }
        binding.mapView.overlays.add(myLocationOverlay)
    }

    private fun setupListeners() {
        binding.btnRecenter.setOnClickListener {
            val grandCentral = GeoPoint(40.7527, -73.9772)
            binding.mapView.controller.animateTo(grandCentral)
        }

        binding.btnArmWakeZone.setOnClickListener {
            viewModel.toggleArmState()
        }

        binding.switchSilentNap.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleSilentNap(isChecked)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.isArmed) {
                        binding.btnArmWakeZone.setText(R.string.home_btn_arm_wake_zone)
                    } else {
                        binding.btnArmWakeZone.text = getString(R.string.home_btn_arm_wake_zone)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        myLocationOverlay?.enableMyLocation()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        myLocationOverlay?.disableMyLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDetach()
        _binding = null
    }
}
