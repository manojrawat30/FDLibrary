package com.nivesh.production.bajajfd.ui.providerfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nivesh.production.bajajfd.repositories.MainRepository
import com.nivesh.production.bajajfd.viewModel.BajajFDViewModel

class FDModelProviderFactory(private val mainRepository: MainRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BajajFDViewModel(mainRepository) as T
    }

}