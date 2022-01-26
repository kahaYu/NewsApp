package com.yurakolesnikov.countriesapp.ui

import android.app.Application
import android.media.ApplicationMediaCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yurakolesnikov.countriesapp.repository.NewsRepository

class NewsViewModelProviderFactory (
    val app: Application,
    val newsRepository: NewsRepository
        ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository, app) as T
    }
}