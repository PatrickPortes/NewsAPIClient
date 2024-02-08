package com.example.newsapiclient.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.newsapiclient.R
import com.example.newsapiclient.databinding.ActivityMainBinding
import com.example.newsapiclient.presentation.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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