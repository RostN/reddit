package com.example.finalattestation.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finalattestation.R
import com.example.finalattestation.databinding.FragmentFavoriteSubredditBinding
import com.google.android.material.tabs.TabLayout

class FavoriteFragmentSubreddit : Fragment() {
    private var _binding: FragmentFavoriteSubredditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteSubredditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentList = listOf(
            FavoriteFragmentSubredditAll.newInstance(),
            FavoriteFragmentSubredditSaved.newInstance()
        )
        //Костыль, чтобы активировать первую вкладку на старте
        binding.tabLayout.addTab(binding.tabLayout.newTab(),fragmentList.size,true)
        binding.tabLayout.removeTabAt(fragmentList.size)

        //Настройка таблайаута
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, fragmentList[tab?.position!!]).commit()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        binding.tabLayout.getTabAt(0)?.select()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragmentSubreddit()
    }
}