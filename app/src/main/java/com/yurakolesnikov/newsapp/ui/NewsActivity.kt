package com.yurakolesnikov.newsapp.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.WindowInsetsController
import android.webkit.WebSettings
import android.webkit.WebSettings.FORCE_DARK_AUTO
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.yurakolesnikov.newsapp.R
import com.yurakolesnikov.newsapp.databinding.ActivityNewsBinding
import com.yurakolesnikov.newsapp.db.ArticleDatabase
import com.yurakolesnikov.newsapp.repository.NewsRepository
import com.yurakolesnikov.newsapp.utils.Mode
import com.yurakolesnikov.newsapp.utils.hideSystemUI
import java.security.AccessController.getContext

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewsBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        hideSystemUI()
        supportActionBar?.hide()

        binding.cbMode.isChecked = isDarkModeOn()

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            !isDarkModeOn()

        binding.cbMode.setOnClickListener {
            if (isDarkModeOn()) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            }
        }

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        viewModel.currentOrientation = resources.configuration.orientation

        if (isDarkModeOn()) viewModel.currentMode = Mode.NIGHT
        else viewModel.currentMode = Mode.DAY

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)

    }

    override fun onDestroy() {
        super.onDestroy()
            viewModel.previousOrientation = viewModel.currentOrientation
            viewModel.previousMode = viewModel.currentMode

    }

    fun isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}