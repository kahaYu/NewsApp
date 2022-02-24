package com.yurakolesnikov.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.snackbar.Snackbar
import com.yurakolesnikov.newsapp.databinding.FragmentArticleBinding
import com.yurakolesnikov.newsapp.ui.NewsViewModel
import com.yurakolesnikov.newsapp.ui.NewsActivity
import com.yurakolesnikov.newsapp.utils.AutoClearedValue

class ArticleFragment : Fragment() {

    private var binding by AutoClearedValue<FragmentArticleBinding>(this) // To forget about lifecycle
    lateinit var vm: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = (activity as NewsActivity).viewModel
        // Mode of WebView have to be managed separately from activity
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && (activity as NewsActivity).isDarkModeOn()) {
            WebSettingsCompat.setForceDark(
                binding.webView.settings,
                WebSettingsCompat.FORCE_DARK_ON
            )
        }

        if (vm.isTransactionFromSavedNewsFragment) { // Logic to remove fab when article saved only
            binding.fab.visibility = View.GONE
            vm.isTransactionFromSavedNewsFragment = false
        }

        binding.webView.apply {
            webViewClient = WebViewClient()
            vm.articleForArticleFragment?.let { loadUrl(it.url ?: "") }
        }

        binding.fab.setOnClickListener {
            vm.articleForArticleFragment?.let { vm.saveArticle(it) }
            Snackbar.make(view, "Article saved", Snackbar.LENGTH_SHORT).show()
        }
    }
}