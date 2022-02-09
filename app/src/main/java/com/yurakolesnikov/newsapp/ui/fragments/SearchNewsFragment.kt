package com.yurakolesnikov.newsapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
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

    var isLoading = false

    var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        binding.etSearch.setText(viewModel.lastSearchQuery)
        newsAdapter.differ.submitList(viewModel.searchNewsResponse?.articles)

        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    viewModel.searchNewsPage = 1
                    viewModel.searchNewsResponse = null
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
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        if (binding.etSearch.text.isNotEmpty()) {
                            newsAdapter.differ.submitList(newsResponse.articles.toList())
                            val rangeStart =
                                (viewModel.searchNewsPage - 2) * Constants.QUERY_PAGE_SIZE + 1
                            val itemCount = newsResponse.articles.size - rangeStart + 1
                            newsAdapter.notifyItemRangeInserted(rangeStart, itemCount)
                            viewModel.totalResults = newsResponse.totalResults
                            viewModel.totalResults = newsResponse.totalResults
                        } else newsAdapter.differ.submitList(listOf<Article>())
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        viewModel.showSafeToast(requireActivity(), message)
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
        viewModel.searchNews = MutableLiveData()
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
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (viewModel.searchNewsResponse == null
                &&!viewModel.previousInternetStateSearchNews
                && viewModel.hasInternetConnection()) {
                viewModel.searchNews(binding.etSearch.text.toString())
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy != 0) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCountInAdapter = layoutManager.itemCount

                val isAtLastItem = (lastVisibleItemPosition + 1) >= totalItemCountInAdapter
                val isTotalMoreThanVisible = viewModel.totalResults ?: 0 > totalItemCountInAdapter

                val shouldPaginate = isAtLastItem && isTotalMoreThanVisible && !isLoading

                if (shouldPaginate) {
                    viewModel.searchNews(binding.etSearch.text.toString())
                }
            }
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

}