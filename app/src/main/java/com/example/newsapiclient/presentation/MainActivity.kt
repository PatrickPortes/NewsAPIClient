package com.example.newsapiclient.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.newsapiclient.R
import com.example.newsapiclient.databinding.ActivityMainBinding
import com.example.newsapiclient.presentation.adapter.NewsAdapter
import com.example.newsapiclient.presentation.viewmodel.NewsViewModel
import com.example.newsapiclient.presentation.viewmodel.NewsViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: NewsViewModelFactory

    @Inject
    lateinit var newsAdapter: NewsAdapter

    lateinit var viewModel: NewsViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting ViewModel
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        // Setting the start nav fragment
        replaceFragment(NewsFragment())

        // Restore the last selected fragment
        viewModel.selectedFragmentId.takeIf { it != -1 }?.let { selectedFragmentId ->
            binding.bnvNews.selectedItemId = selectedFragmentId
            navigateToFragment(selectedFragmentId)
        }

        // Bottom Navigation Bar Configuration
        binding.bnvNews.setOnItemSelectedListener {
            navigateToFragment(it.itemId)
            true
        }

    }

    private fun navigateToFragment(itemId: Int) {
        viewModel.selectedFragmentId = itemId
        when (itemId) {
            R.id.newsFragment -> replaceFragment(NewsFragment())
            R.id.savedFragment -> replaceFragment(SavedFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()

    }

}