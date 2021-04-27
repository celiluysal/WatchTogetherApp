package com.celiluysal.watchtogetherapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding, VM: ViewModel>: Fragment() {
    protected lateinit var binding: VB
    lateinit var viewModel: VM

//    private val viewModel by lazy {
////        (activity as BaseActivity<*,*>).defaultViewModelProviderFactory.let {
////            ViewModelProvider(this, it).get(viewModelClass)
////        }
//    }

//    abstract fun provideBaseViewModel(): BaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel = ViewModelProvider(this).get(viewModelClass)
//        provideBaseViewModel().apply {
//        }
    }

    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
}

