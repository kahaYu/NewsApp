package com.yurakolesnikov.newsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.yurakolesnikov.newsapp.databinding.FragmentArticleBinding
import com.yurakolesnikov.newsapp.ui.NewsViewModel
import com.yurakolesnikov.newsapp.ui.NewsActivity
import com.yurakolesnikov.newsapp.utils.AutoClearedValue

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

        // Разобраться тут. Вылазит ошибка при переходе на статью из savedFragment
        //val nameOfPreviousFragment = childFragmentManager.getBackStackEntryAt(childFragmentManager.backStackEntryCount - 1).name
        //if (nameOfPreviousFragment == "SavedNewsFragment") binding.fab.visibility = View.GONE

        binding.webView.apply {
            webViewClient = WebViewClient()
            viewModel.articleForArticleFragment?.let { loadUrl(it.url ?: "") }
            }


        binding.fab.setOnClickListener {
            viewModel.articleForArticleFragment?.let { viewModel.saveArticle(it) }
            Snackbar.make(view, "Article saved", Snackbar.LENGTH_SHORT).show()
        }
    }

}