package com.example.pandeyprojectandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToCustomerFragment = view.findViewById<Button>(R.id.goToCustomerButton)
        val goToStaffFragment = view.findViewById<Button>(R.id.goToStaffButton)

        goToCustomerFragment.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_homeFragment_to_customerFragment)
        }

        goToStaffFragment.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_homeFragment_to_staffFragment)
        }

    }


}