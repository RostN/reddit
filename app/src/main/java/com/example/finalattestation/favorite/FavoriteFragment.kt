package com.example.finalattestation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalattestation.R
import com.example.finalattestation.databinding.FragmentFavoriteBinding
import com.google.android.material.tabs.TabLayout

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentList = listOf(
            FavoriteFragmentSubreddit.newInstance(),
            FavoriteFragmentComments.newInstance()
        )

        //Костыль, чтобы активировать первую вкладку на старте
        binding.tabLayout.addTab(binding.tabLayout.newTab(),fragmentList.size,true)
        binding.tabLayout.removeTabAt(fragmentList.size)

        //Настройка таблайаута
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                                parentFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, fragmentList[tab?.position!!]).commit()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
        binding.tabLayout.getTabAt(0)?.select()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}