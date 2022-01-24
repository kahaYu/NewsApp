package com.yurakolesnikov.countriesapp.ui.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.yurakolesnikov.countriesapp.R
import com.yurakolesnikov.countriesapp.databinding.ActivityNewsBinding
import com.yurakolesnikov.countriesapp.db.ArticleDatabase
import com.yurakolesnikov.countriesapp.repository.NewsRepository
import com.yurakolesnikov.countriesapp.ui.NewsViewModel
import com.yurakolesnikov.countriesapp.ui.NewsViewModelProviderFactory
import com.yurakolesnikov.countriesapp.utils.AutoClearedValue
import com.yurakolesnikov.countriesapp.utils.hideSystemUI

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel : NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewsBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        hideSystemUI()
        supportActionBar?.hide()

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
    }
}