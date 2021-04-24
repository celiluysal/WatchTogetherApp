package com.celiluysal.watchtogetherapp.ui.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.base.BaseFragment
import com.celiluysal.watchtogetherapp.databinding.ProfileFragmentBinding
import com.celiluysal.watchtogetherapp.databinding.RoomsFragmentBinding
import com.celiluysal.watchtogetherapp.ui.dialog.AvatarPickerDialog

class RoomsFragment : BaseFragment<RoomsFragmentBinding, RoomsViewModel>() {

    companion object {
        fun newInstance() = RoomsFragment()
    }

//    private lateinit var viewModel: RoomsViewModel
//    private lateinit var binding: RoomsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RoomsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): RoomsFragmentBinding {
        return RoomsFragmentBinding.inflate(inflater, container, false)
    }

}