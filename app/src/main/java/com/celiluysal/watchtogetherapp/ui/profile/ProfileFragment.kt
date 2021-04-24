package com.celiluysal.watchtogetherapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.lifecycle.ViewModelProvider
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.base.BaseFragment
import com.celiluysal.watchtogetherapp.databinding.ProfileFragmentBinding
import com.celiluysal.watchtogetherapp.ui.dialog.AvatarImage
import com.celiluysal.watchtogetherapp.ui.dialog.AvatarPickerDialog
import com.celiluysal.watchtogetherapp.ui.login.LoginActivity
import com.celiluysal.watchtogetherapp.utils.WTSessionManager
import com.celiluysal.watchtogetherapp.utils.WTUtils


class ProfileFragment : BaseFragment<ProfileFragmentBinding, ProfileViewModel>(),
    AvatarPickerDialog.onUpdateButtonClickListener {

    private lateinit var avatarPickerDialog: AvatarPickerDialog

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        setChangePasswordLayout()

        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
            activity?.let {
                it.startActivity(Intent(it, LoginActivity::class.java))
                it.finish()
            }
        }

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        viewModel.refresh()
        observeViewModel()

    }


    private fun observeViewModel() {
        viewModel.wtUser.observe(viewLifecycleOwner, { wtUser ->
            binding.textViewFullName.text = wtUser.fullName
            binding.textViewEmail.text = wtUser.email

            binding.avatarContainer.imageViewAvatar.setImageResource(
                WTUtils.shared.getAvatarResId(activity?.baseContext, wtUser.avatarId)
            )

            binding.avatarContainer.imageViewEditAvatar.setOnClickListener {
                activity?.supportFragmentManager?.let {

                    avatarPickerDialog = AvatarPickerDialog(wtUser.avatarId, this)
                    avatarPickerDialog.show(it, "AvatarPickerDialog")

                }
            }
        }
        )
    }

    override fun onUpdateButtonClick(item: AvatarImage) {
        viewModel.updateAvatar(item.id)
        avatarPickerDialog.dismiss()
        Toast.makeText(activity, "Avatar Güncellendi", Toast.LENGTH_SHORT).show()
    }

    private fun setChangePasswordLayout() {
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
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ProfileFragmentBinding {
        return ProfileFragmentBinding.inflate(inflater, container, false)
    }


}