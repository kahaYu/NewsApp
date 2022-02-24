package com.yurakolesnikov.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yurakolesnikov.newsapp.R
import com.yurakolesnikov.newsapp.adapters.NewsAdapter
import com.yurakolesnikov.newsapp.databinding.FragmentBreakingNewsBinding
import com.yurakolesnikov.newsapp.ui.NewsViewModel
import com.yurakolesnikov.newsapp.ui.NewsActivity
import com.yurakolesnikov.newsapp.utils.AutoClearedValue
import com.yurakolesnikov.newsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.yurakolesnikov.newsapp.utils.Resource
import java.util.*

class BreakingNewsFragment : Fragment() {

    private var binding by AutoClearedValue<FragmentBreakingNewsBinding>(this)

    private lateinit var vm: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private var isLoading = false

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

        vm = (activity as NewsActivity).viewModel

        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            vm.articleForArticleFragment = it
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment)
        }
        // Prepopulate rv, if there are articles in viewModel
        newsAdapter.differ.submitList(vm.breakingNewsResponse?.articles)

        vm.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val rangeStart = (vm.breakingNewsPage - 2) * QUERY_PAGE_SIZE
                        val itemCount = newsResponse.articles.size - rangeStart
                        newsAdapter.notifyItemRangeInserted(rangeStart, itemCount)
                        vm.totalResults = newsResponse.totalResults
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        showSafeToast(view, message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vm.breakingNews = MutableLiveData()
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // Preparations to know should paginate or not
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            val totalItemCountInAdapter = layoutManager.itemCount

            val isAtLastItem = (lastVisibleItemPosition + 1) >= totalItemCountInAdapter
            val isTotalMoreThanVisible = vm.totalResults ?: 0 > totalItemCountInAdapter
            // Three conditions, when should paginate
            val shouldPaginate = isAtLastItem && isTotalMoreThanVisible && !isLoading

            if (shouldPaginate) {
                vm.getBreakingNews("us") // With already increased page in vm
            }

            // If connection appear, all we need for refreshing page is to swipe screen
            if (vm.breakingNewsResponse == null
                && !vm.previousInternetStateBreakingNews
                && vm.hasInternetConnection()
            ) {
                vm.getBreakingNews("us") // Call only if internet is ok
            }
            else
                if (vm.breakingNewsResponse == null && !vm.hasInternetConnection()) {
                    vm.breakingNews.postValue(Resource.Error("no internet connection"))
                }

            if (vm.isTooManyRequests)
                vm.breakingNews.postValue(Resource.Error("too many requests"))
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy) // Can't put above's code here, cuz onScrolled
            // is only called when visible ViewHolders are moved. If we have not ViewHolders at
            // all, we can't use this method
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
            setHasFixedSize(true) // Accelerate work of rv
        }
    }

    private fun showSafeToast(view: View, text: String) { // Prevent appearance of multiply toasts at once
        if (Calendar.getInstance().timeInMillis >= vm.toastShowTime + 4000L) {
            val snackbar = Snackbar.make(view, "Error: $text", Snackbar.LENGTH_LONG)
            snackbar.view.background = resources.getDrawable(R.drawable.snackbar_background)
            snackbar.show()
            vm.toastShowTime = Calendar.getInstance().timeInMillis
        }
    }
}