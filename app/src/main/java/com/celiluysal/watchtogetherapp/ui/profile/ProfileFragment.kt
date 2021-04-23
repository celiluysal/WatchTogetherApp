package com.celiluysal.watchtogetherapp.ui.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.celiluysal.watchtogetherapp.base.BaseFragment
import com.celiluysal.watchtogetherapp.databinding.ProfileFragmentBinding
import com.celiluysal.watchtogetherapp.ui.login.LoginActivity
import com.celiluysal.watchtogetherapp.utils.WTSessionManager

class ProfileFragment : BaseFragment<ProfileFragmentBinding, ProfileViewModel>() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.layoutPassword.visibility = LinearLayout.GONE
        var showPasswordLayout = false
        binding.buttonUpdatePassword.setOnClickListener { _ ->
            if (showPasswordLayout) {
                binding.layoutPassword.visibility = LinearLayout.GONE
                showPasswordLayout = false
            } else {
                binding.layoutPassword.visibility = LinearLayout.VISIBLE
                showPasswordLayout = true
            }
        }

        binding.buttonLogout.setOnClickListener {
            WTSessionManager.shared.logOut()
            activity?.let {
                it.startActivity(Intent(it, LoginActivity::class.java))
                it.finish()
            }
        }


        binding.textViewFullName.text = WTSessionManager.shared.user.fullName
        binding.textViewEmail.text = WTSessionManager.shared.user.email


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        viewModel.refresh()
        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.wtUser.observe(viewLifecycleOwner, {
            binding.textViewFullName.text = it.fullName
            binding.textViewEmail.text = it.email
        }
        )
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ProfileFragmentBinding {
        return ProfileFragmentBinding.inflate(inflater, container, false)
    }

}