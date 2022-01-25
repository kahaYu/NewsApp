package com.yurakolesnikov.countriesapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.yurakolesnikov.countriesapp.databinding.FragmentArticleBinding
import com.yurakolesnikov.countriesapp.databinding.FragmentBreakingNewsBinding
import com.yurakolesnikov.countriesapp.ui.NewsViewModel
import com.yurakolesnikov.countriesapp.ui.mainActivity.NewsActivity
import com.yurakolesnikov.countriesapp.utils.AutoClearedValue

class ArticleFragment : Fragment() {

    private var binding by AutoClearedValue<FragmentArticleBinding>(this)

    lateinit var viewModel: NewsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        binding.webView.apply {
            webViewClient = WebViewClient()
                loadUrl(viewModel.articleUrlForActicleFragment)
        }
    }

}