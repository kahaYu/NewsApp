package com.yurakolesnikov.countriesapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yurakolesnikov.countriesapp.databinding.FragmentBreakingNewsBinding
import com.yurakolesnikov.countriesapp.databinding.FragmentSavedNewsBinding
import com.yurakolesnikov.countriesapp.databinding.FragmentSearchNewsBinding
import com.yurakolesnikov.countriesapp.utils.AutoClearedValue

class SearchNewsFragment : Fragment() {

    private var binding by AutoClearedValue<FragmentSearchNewsBinding>(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root

    }

}