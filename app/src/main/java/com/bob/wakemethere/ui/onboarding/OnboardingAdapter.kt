package com.bob.wakemethere.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val permissionTypes = listOf(
        PermissionType.FINE_LOCATION,
        PermissionType.BACKGROUND_LOCATION,
        PermissionType.POST_NOTIFICATIONS,
    )

    override fun getItemCount(): Int = permissionTypes.size

    override fun createFragment(position: Int): Fragment {
        return PermissionSlideFragment.newInstance(permissionTypes[position])
    }
}
