package com.yurakolesnikov.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yurakolesnikov.newsapp.R
import com.yurakolesnikov.newsapp.adapters.NewsAdapter
import com.yurakolesnikov.newsapp.databinding.FragmentBreakingNewsBinding
import com.yurakolesnikov.newsapp.ui.NewsViewModel
import com.yurakolesnikov.newsapp.ui.NewsActivity
import com.yurakolesnikov.newsapp.utils.AutoClearedValue
import com.yurakolesnikov.newsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.yurakolesnikov.newsapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class BreakingNewsFragment : Fragment() {

    private var binding by AutoClearedValue<FragmentBreakingNewsBinding>(this)

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    var isLoading = false


    val TAG = "BreakingNewsFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            viewModel.articleForArticleFragment = it
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment)
        }

        newsAdapter.differ.submitList(viewModel.breakingNewsResponse?.articles)
        //if (viewModel.breakingNewsResponse == null) viewModel.getBreakingNews("us")

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val rangeStart = (viewModel.breakingNewsPage - 2) * QUERY_PAGE_SIZE + 1
                        val itemCount = newsResponse.articles.size - rangeStart + 1
                        newsAdapter.notifyItemRangeInserted(rangeStart, itemCount)
                        viewModel.totalResults = newsResponse.totalResults
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        viewModel.showSafeToast(requireActivity(), message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
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

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            val totalItemCountInAdapter = layoutManager.itemCount

            val isAtLastItem = (lastVisibleItemPosition + 1) >= totalItemCountInAdapter
            val isTotalMoreThanVisible = viewModel.totalResults ?: 0 > totalItemCountInAdapter

            val shouldPaginate = isAtLastItem && isTotalMoreThanVisible && !isLoading

            if (shouldPaginate) {
                viewModel.getBreakingNews("us")
            }

            // If connection appear, all we need to refresh page is to swipe screen
            if (viewModel.breakingNewsResponse == null
                && !viewModel.previousInternetStateBreakingNews
                && viewModel.hasInternetConnection()
            ) {
                viewModel.getBreakingNews("us")
            }
            when (viewModel.breakingNewsResponse) {
                null -> if (!viewModel.hasInternetConnection())
                    viewModel.breakingNews.postValue(Resource.Error("no internet connection"))
                else -> if (!viewModel.hasInternetConnection() && isAtLastItem)
                    viewModel.breakingNews.postValue(Resource.Error("no internet connection"))
            }

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.breakingNews = MutableLiveData()
    }
}