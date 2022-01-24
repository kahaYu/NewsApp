package com.yurakolesnikov.countriesapp.ui

import androidx.lifecycle.ViewModel
import com.yurakolesnikov.countriesapp.repository.NewsRepository

class NewsViewModel(
    val repository: NewsRepository
    ) : ViewModel() {
}