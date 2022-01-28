package com.yurakolesnikov.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yurakolesnikov.newsapp.R
import com.yurakolesnikov.newsapp.adapters.NewsAdapter
import com.yurakolesnikov.newsapp.databinding.FragmentSearchNewsBinding
import com.yurakolesnikov.newsapp.models.Article
import com.yurakolesnikov.newsapp.ui.NewsViewModel
import com.yurakolesnikov.newsapp.ui.NewsActivity
import com.yurakolesnikov.newsapp.utils.AutoClearedValue
import com.yurakolesnikov.newsapp.utils.Constants
import com.yurakolesnikov.newsapp.utils.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.yurakolesnikov.newsapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment() {

    private var binding by AutoClearedValue<FragmentSearchNewsBinding>(this)

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "SearchNewsFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            viewModel.articleForArticleFragment = it
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment)
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    viewModel.searchNewsPage = 1
                    viewModel.searchNewsResponse = null
                    if (editable.toString().isNotEmpty() && editable.toString() != viewModel.lastSearchQuery) {
                        viewModel.searchNews(editable.toString())
                    } else if (editable.toString().isEmpty()) newsAdapter.differ.submitList(listOf<Article>())
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        if (binding.etSearch.text.isEmpty()) newsAdapter.differ.submitList(listOf<Article>())
                        else newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occurred: $message", Toast.LENGTH_LONG).show()
                        if (message != "Too many requests today" && viewModel.hasInternetConnection()) {
                            viewModel.searchNews(binding.etSearch.text.toString())
                        }
                    }
                }
                is Resource.Loading -> {
                    viewModel.lastSearchQuery = binding.etSearch.text.toString()
                    showProgressBar()
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.lastSearchQuery = ""
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible
            if (shouldPaginate) viewModel.searchNews(binding.etSearch.text.toString())
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

}