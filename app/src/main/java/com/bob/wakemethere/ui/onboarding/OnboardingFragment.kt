package com.bob.wakemethere.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bob.wakemethere.R
import com.bob.wakemethere.databinding.FragmentOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

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
                    updateUiForPage(position)
                }
            },
        )

        binding.btnBack.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem > 0) {
                binding.viewPager.currentItem = currentItem - 1
            }
        }

        binding.btnSkip.setOnClickListener {
            navigateToHome()
        }

        binding.btnMainAction.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            val currentFragment = childFragmentManager.findFragmentByTag("f$currentItem") as? PermissionSlideFragment
            currentFragment?.requestSlidePermission()

            if (currentItem < (adapter.itemCount - 1)) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                navigateToHome()
            }
        }
    }

    private fun updateUiForPage(position: Int) {
        when (position) {
            0 -> {
                binding.layoutLogo.isVisible = true
                binding.btnBack.isVisible = false
                binding.tvTopChip.isVisible = false
                binding.btnSkip.isVisible = true
                binding.btnMainAction.text = getString(R.string.btn_slide1)
            }
            1 -> {
                binding.layoutLogo.isVisible = false
                binding.btnBack.isVisible = true
                binding.tvTopChip.isVisible = true
                binding.tvTopChip.text = getString(R.string.top_chip_slide2)
                binding.btnSkip.isVisible = false
                binding.btnMainAction.text = getString(R.string.btn_slide2)
            }
            2 -> {
                binding.layoutLogo.isVisible = false
                binding.btnBack.isVisible = true
                binding.tvTopChip.isVisible = true
                binding.tvTopChip.text = getString(R.string.top_chip_slide3)
                binding.btnSkip.isVisible = false
                binding.btnMainAction.text = getString(R.string.btn_slide3)
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
