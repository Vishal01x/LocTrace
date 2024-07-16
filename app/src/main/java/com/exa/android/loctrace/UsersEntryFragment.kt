package com.exa.android.loctrace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.exa.android.loctrace.databinding.FragmentUsersEntryBinding

class UsersEntryFragment : Fragment() {

    private var _binding:FragmentUsersEntryBinding?=null
    private  val binding get()=_binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentUsersEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.adminButton.setOnClickListener {
            findNavController().navigate(R.id.action_usersEntryFragment_to_employesLocation)
        }
        binding.employeeButton.setOnClickListener {
            findNavController().navigate(R.id.action_usersEntryFragment_to_userLocationService)
        }
    }
}