package com.yurakolesnikov.countriesapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yurakolesnikov.countriesapp.R
import com.yurakolesnikov.countriesapp.adapters.NewsAdapter
import com.yurakolesnikov.countriesapp.databinding.FragmentBreakingNewsBinding
import com.yurakolesnikov.countriesapp.databinding.FragmentSavedNewsBinding
import com.yurakolesnikov.countriesapp.ui.NewsViewModel
import com.yurakolesnikov.countriesapp.ui.mainActivity.NewsActivity
import com.yurakolesnikov.countriesapp.utils.AutoClearedValue

class SavedNewsFragment : Fragment() {

    private var binding by AutoClearedValue<FragmentSavedNewsBinding>(this)

    lateinit var viewModel : NewsViewModel
    lateinit var newsAdapter : NewsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment, bundle)
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}