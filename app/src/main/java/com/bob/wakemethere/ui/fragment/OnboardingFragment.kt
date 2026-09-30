package com.bob.wakemethere.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bob.wakemethere.R
import com.bob.wakemethere.data.model.OnboardingNavigationEvent
import com.bob.wakemethere.databinding.FragmentOnboardingBinding
import com.bob.wakemethere.ui.adapter.OnboardingAdapter
import com.bob.wakemethere.ui.viewmodel.OnboardingViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OnboardingAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.onPageChanged(position)
                }
            },
        )

        binding.btnBack.setOnClickListener {
            viewModel.onBackClicked()
        }

        binding.btnSkip.setOnClickListener {
            viewModel.onSkipClicked()
        }

        binding.btnMainAction.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            val currentFragment = childFragmentManager.findFragmentByTag("f$currentItem") as? PermissionSlideFragment
            currentFragment?.requestSlidePermission()

            viewModel.onMainActionClicked()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->
                        binding.layoutLogo.isVisible = uiState.isFirstPage
                        binding.btnBack.isVisible = !uiState.isFirstPage
                        binding.btnSkip.isVisible = uiState.isFirstPage

                        if (uiState.topChipTextRes != null) {
                            binding.tvTopChip.isVisible = true
                            binding.tvTopChip.setText(uiState.topChipTextRes)
                        } else {
                            binding.tvTopChip.isVisible = false
                        }

                        binding.btnMainAction.setText(uiState.actionButtonTextRes)
                    }
                }

                launch {
                    viewModel.navigationEvent.collect { event ->
                        when (event) {
                            is OnboardingNavigationEvent.NavigateToHome -> {
                                findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment)
                            }
                            is OnboardingNavigationEvent.ScrollToPage -> {
                                binding.viewPager.currentItem = event.page
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
