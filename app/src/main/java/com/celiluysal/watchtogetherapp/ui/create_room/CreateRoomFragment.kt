package com.celiluysal.watchtogetherapp.ui.create_room

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.databinding.CreateRoomFragmentBinding

class CreateRoomFragment : Fragment() {


    companion object {
        fun newInstance() = CreateRoomFragment()
    }

    private lateinit var viewModel: CreateRoomViewModel
    private lateinit var binding: CreateRoomFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateRoomFragmentBinding.inflate(inflater, container, false)

        binding.switchPublic.isChecked = true
        binding.layoutPassword.visibility = LinearLayout.GONE

        binding.switchPublic.setOnClickListener {
            if (binding.switchPublic.isChecked)
                binding.layoutPassword.visibility = LinearLayout.GONE
            else
                binding.layoutPassword.visibility = LinearLayout.VISIBLE
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateRoomViewModel::class.java)
        // TODO: Use the ViewModel
    }

}