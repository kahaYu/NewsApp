package com.yurakolesnikov.countriesapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yurakolesnikov.countriesapp.R
import com.yurakolesnikov.countriesapp.adapters.NewsAdapter
import com.yurakolesnikov.countriesapp.databinding.FragmentSavedNewsBinding
import com.yurakolesnikov.countriesapp.databinding.FragmentSearchNewsBinding
import com.yurakolesnikov.countriesapp.ui.NewsViewModel
import com.yurakolesnikov.countriesapp.ui.mainActivity.NewsActivity
import com.yurakolesnikov.countriesapp.utils.AutoClearedValue
import com.yurakolesnikov.countriesapp.utils.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.yurakolesnikov.countriesapp.utils.Resource
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
            viewModel.articleUrlForActicleFragment = it.url
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment)
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty() && editable.toString() != viewModel.lastSearchQuery) {
                        viewModel.searchNews(editable.toString())
                    } else if (editable.toString().isEmpty()) newsAdapter.differ.submitList(emptyList())
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        if (binding.etSearch.text.isEmpty()) newsAdapter.differ.submitList(emptyList())
                        else newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occurred: $message")
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
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}