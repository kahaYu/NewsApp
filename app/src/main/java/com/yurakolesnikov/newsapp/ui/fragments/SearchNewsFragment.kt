package com.yurakolesnikov.newsapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yurakolesnikov.newsapp.R
import com.yurakolesnikov.newsapp.adapters.NewsAdapter
import com.yurakolesnikov.newsapp.databinding.FragmentSearchNewsBinding
import com.yurakolesnikov.newsapp.models.Article
import com.yurakolesnikov.newsapp.ui.NewsViewModel
import com.yurakolesnikov.newsapp.ui.NewsActivity
import com.yurakolesnikov.newsapp.utils.AutoClearedValue
import com.yurakolesnikov.newsapp.utils.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.yurakolesnikov.newsapp.utils.Resource
import com.yurakolesnikov.newsapp.utils.hideKeyboard
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SearchNewsFragment : Fragment() {

    private var binding by AutoClearedValue<FragmentSearchNewsBinding>(this)

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    var isLoading = false // Check if loading now

    var job: Job? = null // Background search job

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) hideKeyboard() // Remove keyboard when touch out of editText field
        }

        newsAdapter.setOnItemClickListener {
            viewModel.articleForArticleFragment = it // Save article to show in ArticleFragment
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment)
        }
        // If the query was previously, recover this query
        binding.etSearch.setText(viewModel.lastSearchQuery)
        newsAdapter.differ.submitList(viewModel.searchNewsResponse?.articles)

        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel() // Cancel previous job if new char is entered
            if (viewModel.previousOrientation == viewModel.currentOrientation // Prevent firing change
                && viewModel.previousMode == viewModel.currentMode // listener in these two cases
            ) {
                job = MainScope().launch {
                    delay(SEARCH_NEWS_TIME_DELAY) // Search starts after some delay
                    editable?.let {
                        if (editable.toString() != viewModel.lastSearchQuery) {
                            viewModel.searchNewsPage = 1 // Drawback the original values of page and
                            viewModel.searchNewsResponse = null // response
                        }
                        if (editable.toString()
                                .isNotEmpty() && editable.toString() != viewModel.lastSearchQuery
                        ) {
                            viewModel.searchNews(editable.toString())
                        } else if (editable.toString().isEmpty()) {
                            newsAdapter.differ.submitList(listOf<Article>())
                            viewModel.lastSearchQuery = ""
                        }
                    }
                }
            } else {
                viewModel.previousOrientation = viewModel.currentOrientation
                viewModel.previousMode = viewModel.currentMode
            }

        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        if (binding.etSearch.text.isNotEmpty()) {
                            newsAdapter.differ.submitList(newsResponse.articles.toList())
                            // Too complicated to track deletions, updates etc.
                            // We don't have much results, this method won't affect to productivity
                            newsAdapter.notifyDataSetChanged()
                            viewModel.totalResults = newsResponse.totalResults
                        } else newsAdapter.differ.submitList(listOf<Article>())
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        showSafeToast(view, message)
                    }
                }
                is Resource.Loading -> {
                    viewModel.lastSearchQuery = binding.etSearch.text.toString()
                    showProgressBar()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.searchNews =
            MutableLiveData() // Reset to prevent firing of old value when fragment created
        job?.cancel()
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        @SuppressLint("ClickableViewAccessibility")
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // Preparations to know should paginate or not
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            val totalItemCountInAdapter = layoutManager.itemCount

            val isAtLastItem = (lastVisibleItemPosition + 1) >= totalItemCountInAdapter
            val isTotalMoreThanVisible = viewModel.totalResults ?: 0 > totalItemCountInAdapter
            // Three conditions, when should paginate
            val shouldPaginate = isAtLastItem && isTotalMoreThanVisible && !isLoading

            if (shouldPaginate) {
                viewModel.searchNews(binding.etSearch.text.toString())
            }

            // If connection appear, all we need for refreshing page is to swipe screen
            if (viewModel.searchNewsResponse == null
                && !viewModel.previousInternetStateSearchNews
                && viewModel.hasInternetConnection()
            ) {
                viewModel.searchNews(binding.etSearch.text.toString())
            }

            binding.etSearch.clearFocus() // To remove keyboard
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
            setHasFixedSize(true)
        }
    }

    private fun showSafeToast(view: View, text: String) {
        if (Calendar.getInstance().timeInMillis >= viewModel.toastShowTime + 4000L) {
            val snackbar = Snackbar.make(view, "Error: $text", Snackbar.LENGTH_LONG)
            snackbar.view.background = resources.getDrawable(R.drawable.snackbar_background)
            snackbar.show()
            viewModel.toastShowTime = Calendar.getInstance().timeInMillis
        }
    }
}