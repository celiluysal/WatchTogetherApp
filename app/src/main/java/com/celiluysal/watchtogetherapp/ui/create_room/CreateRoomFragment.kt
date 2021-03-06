package com.celiluysal.watchtogetherapp.ui.create_room

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.celiluysal.watchtogetherapp.R
import com.celiluysal.watchtogetherapp.databinding.CreateRoomFragmentBinding
import com.celiluysal.watchtogetherapp.ui.room.RoomActivity

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

        setPasswordLayout()

        binding.buttonCreateRoom.setOnClickListener {
            var password: String? = null
            if (!binding.switchPublic.isChecked) {
                if (checkFields()){
                    password = binding.textInputEditTextPassword.text.toString()
                    viewModel.createRoom(binding.textInputEditTextRoomName.text.toString(), password)
                }
            } else
                viewModel.createRoom(binding.textInputEditTextRoomName.text.toString(), password)

            observeViewModel()
        }



        return binding.root
    }

    private fun observeViewModel() {
        viewModel.wtRoom.observe(viewLifecycleOwner, { wtRoom ->
            activity?.let {
                val intent = Intent(context, RoomActivity::class.java)
                intent.putExtra("wtRoomId", wtRoom.roomId)
                it.startActivity(intent)
                it.finish()
            }
        })
    }

    private fun checkFields(): Boolean {
        fun toast(text: String) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

        when {
            binding.textInputEditTextRoomName.text.toString().isEmpty() -> {
                toast(getString(R.string.full_name) + " " + getString(R.string.field_cant_be_empty))
                return false
            }
            else -> {
                when {
                    binding.textInputEditTextPassword.text.toString() == "" && !binding.switchPublic.isChecked -> {
                        toast("??ifre giriniz")
                        return false
                    }
                    binding.textInputEditTextPassword.text.toString().length > 18 -> {
                        toast(getString(R.string.long_password))
                        return false
                    }
                    binding.textInputEditTextPassword.text.toString() != binding.textInputEditTextPasswordAgain.text.toString() -> {
                        toast(getString(R.string.did_not_match_passwords))
                        return false
                    }
                }
                return true
            }
        }
    }


    private fun setPasswordLayout() {
        binding.switchPublic.isChecked = true
        binding.layoutPassword.visibility = LinearLayout.GONE
        binding.switchPublic.setOnClickListener {
            if (binding.switchPublic.isChecked)
                binding.layoutPassword.visibility = LinearLayout.GONE
            else
                binding.layoutPassword.visibility = LinearLayout.VISIBLE
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateRoomViewModel::class.java)
        // TODO: Use the ViewModel
    }

}