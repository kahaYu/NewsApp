package com.yurakolesnikov.newsapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yurakolesnikov.newsapp.R
import com.yurakolesnikov.newsapp.databinding.ActivityNewsBinding
import com.yurakolesnikov.newsapp.db.ArticleDatabase
import com.yurakolesnikov.newsapp.repository.NewsRepository
import com.yurakolesnikov.newsapp.utils.hideSystemUI

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewsBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        hideSystemUI()
        supportActionBar?.hide()

        binding.cbMode.setOnClickListener {
            if ((it as CheckBox).isChecked) AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        viewModel.currentOrientation = resources.configuration.orientation

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.previousOrientation = viewModel.currentOrientation
    }
}