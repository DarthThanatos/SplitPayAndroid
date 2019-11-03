package com.example.splitpayandroid.groups

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter


class GroupsPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) { //FragmentStatePagerAdapter

    override fun getItem(position: Int): Fragment {
        println("Changing tab to position: $position")
        return GroupsFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return 3
    }
}