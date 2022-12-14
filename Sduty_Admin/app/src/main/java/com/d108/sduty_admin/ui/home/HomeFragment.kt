package com.d108.sduty_admin.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.d108.sduty_admin.R
import com.d108.sduty_admin.databinding.FragmentHomeBinding

private const val TAG ="HomeFragment"
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            layoutReportpost.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToReportStoryFragment())
            }
            layoutNotice.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNoticeFragment())
            }
            layoutQna.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToQnAFragment())
            }
            layoutPush.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPushFragment())
            }

        }
    }

}