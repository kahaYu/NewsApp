package com.yurakolesnikov.countriesapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yurakolesnikov.countriesapp.databinding.FragmentBreakingNewsBinding
import com.yurakolesnikov.countriesapp.utils.AutoClearedValue

class ArticleFragment : Fragment() {

    private var binding by AutoClearedValue<FragmentBreakingNewsBinding>(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root

    }

}