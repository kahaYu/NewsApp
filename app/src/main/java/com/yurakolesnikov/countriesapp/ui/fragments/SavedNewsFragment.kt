package com.yurakolesnikov.countriesapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yurakolesnikov.countriesapp.databinding.FragmentBreakingNewsBinding
import com.yurakolesnikov.countriesapp.databinding.FragmentSavedNewsBinding
import com.yurakolesnikov.countriesapp.utils.AutoClearedValue

class SavedNewsFragment : Fragment() {

    private var binding by AutoClearedValue<FragmentSavedNewsBinding>(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding.root

    }

}